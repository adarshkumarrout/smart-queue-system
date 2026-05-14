package com.smartqueue.service;

import com.smartqueue.model.Token;
import com.smartqueue.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoShowDetectionService {

    private final TokenRepository tokenRepository;
    private final TokenService tokenService;

    @Value("${app.queue.no-show-timeout-minutes}")
    private int noShowTimeoutMinutes;

    @Scheduled(fixedDelay = 60000) // every minute
    @Transactional
    public void detectNoShows() {
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(noShowTimeoutMinutes);
        List<Token> candidates = tokenRepository.findNoShowCandidates(timeout);
        for (Token token : candidates) {
            log.info("Auto no-show: token {} queue {}", token.getTokenNumber(), token.getQueue().getId());
            tokenService.markNoShow(token.getId());
        }
    }
}
