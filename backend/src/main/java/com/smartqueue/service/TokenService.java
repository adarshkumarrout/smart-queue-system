package com.smartqueue.service;

import com.smartqueue.cache.RedisCacheService;
import com.smartqueue.dto.request.JoinQueueRequest;
import com.smartqueue.dto.response.TokenResponse;
import com.smartqueue.exception.BusinessException;
import com.smartqueue.exception.ResourceNotFoundException;
// KAFKA ISOLATED - Commented out Kafka imports
// import com.smartqueue.kafka.QueueEventMessage;
// import com.smartqueue.kafka.QueueEventProducer;
import com.smartqueue.model.*;
import com.smartqueue.repository.*;
import com.smartqueue.util.PredictionService;
import com.smartqueue.util.TokenNumberGenerator;
import com.smartqueue.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;
    private final RedisCacheService cacheService;
    // KAFKA ISOLATED - Commented out Kafka producer
    // private final QueueEventProducer kafkaProducer;
    private final PredictionService predictionService;
    private final TokenNumberGenerator tokenNumberGenerator;
    private final WebSocketNotificationService wsNotificationService;

    @Value("${app.rate-limit.max-joins-per-hour}") private int maxJoinsPerHour;

    @Transactional
    public TokenResponse joinQueue(Long userId, JoinQueueRequest request) {
        if (cacheService.isRateLimited(userId, maxJoinsPerHour))
            throw new BusinessException("Rate limit exceeded. Try again later.");

        Queue queue = queueRepository.findById(request.getQueueId())
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        if (queue.getStatus() != Queue.QueueStatus.ACTIVE)
            throw new BusinessException("Queue is not active");

        tokenRepository.findByQueueIdAndUserId(queue.getId(), userId)
                .ifPresent(t -> { if (t.getStatus() == Token.TokenStatus.WAITING)
                    throw new BusinessException("Already in this queue"); });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long waiting = tokenRepository.countWaitingByQueueId(queue.getId());
        if (waiting >= queue.getMaxCapacity())
            throw new BusinessException("Queue is at maximum capacity");

        int position = (int) waiting + 1;
        // VIP/Emergency bump position
        if (request.getPriorityType() == Token.PriorityType.EMERGENCY) position = 1;
        else if (request.getPriorityType() == Token.PriorityType.VIP)
            position = Math.max(1, (int)(waiting / 2));

        int estimatedWait = predictionService.estimateWaitTimeForPosition(queue, position);

        Token token = Token.builder()
                .tokenNumber(tokenNumberGenerator.generate(queue.getName()))
                .queue(queue)
                .user(user)
                .priorityType(request.getPriorityType())
                .positionInQueue(position)
                .estimatedWaitMinutes(estimatedWait)
                .joinedAt(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        token = tokenRepository.save(token);

        cacheService.setQueuePosition(queue.getId(), userId, position);
        cacheService.setQueueSize(queue.getId(), waiting + 1);

        // KAFKA ISOLATED - Commented out Kafka event publishing
        // QueueEventMessage msg = buildMessage("QUEUE_JOIN", token, queue, position, estimatedWait);
        // kafkaProducer.publishJoinEvent(msg);

        return toResponse(token);
    }

    @Transactional
    public TokenResponse serveNextToken(Long queueId) {
        List<Token> waiting = tokenRepository.findByQueueIdAndStatusOrderByPriorityTypeDescCreatedAtAsc(
                queueId, Token.TokenStatus.WAITING);
        if (waiting.isEmpty()) throw new BusinessException("No tokens waiting in queue");

        Token token = waiting.get(0);
        token.setStatus(Token.TokenStatus.SERVED);
        token.setServedAt(LocalDateTime.now());
        tokenRepository.save(token);

        // notify user near turn
        if (waiting.size() > 1) {
            Token next = waiting.get(1);
            wsNotificationService.notifyNearTurn(
                next.getUser().getId(), next.getTokenNumber(), 1);
        }

        // KAFKA ISOLATED - Commented out Kafka event publishing
        // QueueEventMessage msg = buildMessage("QUEUE_SERVED", token,
        //         token.getQueue(), 0, 0);
        // kafkaProducer.publishServedEvent(msg);

        return toResponse(token);
    }

    @Transactional
    public TokenResponse markNoShow(Long tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
        token.setStatus(Token.TokenStatus.NO_SHOW);
        token.setNoShowAt(LocalDateTime.now());
        tokenRepository.save(token);

        // KAFKA ISOLATED - Commented out Kafka event publishing
        // QueueEventMessage msg = buildMessage("QUEUE_NO_SHOW", token,
        //         token.getQueue(), 0, 0);
        // kafkaProducer.publishNoShowEvent(msg);
        return toResponse(token);
    }

    public List<TokenResponse> getUserTokens(Long userId) {
        return tokenRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<TokenResponse> getQueueTokens(Long queueId, Token.TokenStatus status) {
        List<Token> tokens = status != null
                ? tokenRepository.findByQueueIdAndStatus(queueId, status)
                : tokenRepository.findByQueueIdAndStatusOrderByPriorityTypeDescCreatedAtAsc(queueId, Token.TokenStatus.WAITING);
        return tokens.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // KAFKA ISOLATED - Commented out buildMessage method (no longer used)
    /*
    private QueueEventMessage buildMessage(String type, Token token, Queue queue, int position, int wait) {
        return QueueEventMessage.builder()
                .eventType(type)
                .queueId(queue.getId())
                .tokenId(token.getId())
                .userId(token.getUser().getId())
                .branchId(queue.getBranch().getId())
                .businessId(queue.getBranch().getBusiness().getId())
                .tokenNumber(token.getTokenNumber())
                .position(position)
                .waitTimeMinutes(wait)
                .timestamp(LocalDateTime.now())
                .build();
    }
    */

    public TokenResponse toResponse(Token token) {
        return TokenResponse.builder()
                .id(token.getId())
                .tokenNumber(token.getTokenNumber())
                .queueId(token.getQueue().getId())
                .queueName(token.getQueue().getName())
                .userId(token.getUser().getId())
                .username(token.getUser().getUsername())
                .status(token.getStatus())
                .priorityType(token.getPriorityType())
                .positionInQueue(token.getPositionInQueue())
                .estimatedWaitMinutes(token.getEstimatedWaitMinutes())
                .joinedAt(token.getJoinedAt())
                .servedAt(token.getServedAt())
                .build();
    }
}
