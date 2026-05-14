package com.smartqueue.websocket;

import com.smartqueue.dto.response.QueueResponse;
import com.smartqueue.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final QueueService queueService;

    public void notifyQueueUpdate(Long queueId) {
        try {
            QueueResponse snapshot = queueService.getQueueSnapshot(queueId);
            messagingTemplate.convertAndSend("/topic/queue/" + queueId, snapshot);
            log.debug("Sent WS update for queue {}", queueId);
        } catch (Exception e) {
            log.error("WS notification error for queue {}: {}", queueId, e.getMessage());
        }
    }

    public void notifyUserTokenUpdate(Long userId, Object payload) {
        messagingTemplate.convertAndSendToUser(
            String.valueOf(userId), "/queue/token-update", payload);
    }

    public void notifyNearTurn(Long userId, String tokenNumber, int position) {
        NotificationPayload payload = NotificationPayload.builder()
                .type("NEAR_TURN")
                .message("Your token " + tokenNumber + " is " + position + " position(s) away!")
                .tokenNumber(tokenNumber)
                .position(position)
                .build();
        messagingTemplate.convertAndSendToUser(
            String.valueOf(userId), "/queue/notification", payload);
    }
}
