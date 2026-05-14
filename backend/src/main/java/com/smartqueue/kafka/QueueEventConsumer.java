package com.smartqueue.kafka;

/*
 * KAFKA ISOLATED - This Kafka consumer has been commented out
 * The application now works without consuming queue events from Kafka.
 * Real-time updates still work through WebSockets and Redis caching.
 */

/*
import com.smartqueue.cache.RedisCacheService;
import com.smartqueue.config.KafkaConfig;
import com.smartqueue.model.QueueEvent;
import com.smartqueue.repository.QueueEventRepository;
import com.smartqueue.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueEventConsumer {

    private final QueueEventRepository queueEventRepository;
    private final RedisCacheService cacheService;
    private final WebSocketNotificationService wsNotificationService;

    @KafkaListener(topics = KafkaConfig.TOPIC_QUEUE_JOIN, groupId = "smartqueue-group")
    public void handleJoinEvent(QueueEventMessage msg) {
        log.info("Consuming join event: queue={} token={}", msg.getQueueId(), msg.getTokenNumber());
        persistEvent(msg, "QUEUE_JOIN");
        cacheService.setQueuePosition(msg.getQueueId(), msg.getUserId(), msg.getPosition());
        cacheService.setQueueWaitTime(msg.getQueueId(), msg.getWaitTimeMinutes());
        wsNotificationService.notifyQueueUpdate(msg.getQueueId());
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_QUEUE_SERVED, groupId = "smartqueue-group")
    public void handleServedEvent(QueueEventMessage msg) {
        log.info("Consuming served event: token={}", msg.getTokenNumber());
        persistEvent(msg, "QUEUE_SERVED");
        cacheService.invalidateQueue(msg.getQueueId());
        cacheService.invalidateUserPosition(msg.getQueueId(), msg.getUserId());
        wsNotificationService.notifyQueueUpdate(msg.getQueueId());
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_QUEUE_NO_SHOW, groupId = "smartqueue-group")
    public void handleNoShowEvent(QueueEventMessage msg) {
        log.info("Consuming no-show event: token={}", msg.getTokenNumber());
        persistEvent(msg, "QUEUE_NO_SHOW");
        cacheService.invalidateQueue(msg.getQueueId());
        cacheService.invalidateUserPosition(msg.getQueueId(), msg.getUserId());
        wsNotificationService.notifyQueueUpdate(msg.getQueueId());
    }

    private void persistEvent(QueueEventMessage msg, String eventType) {
        QueueEvent event = QueueEvent.builder()
                .eventType(eventType)
                .queueId(msg.getQueueId())
                .tokenId(msg.getTokenId())
                .userId(msg.getUserId())
                .branchId(msg.getBranchId())
                .businessId(msg.getBusinessId())
                .tokenNumber(msg.getTokenNumber())
                .position(msg.getPosition())
                .waitTimeMinutes(msg.getWaitTimeMinutes())
                .timestamp(LocalDateTime.now())
                .build();
        queueEventRepository.save(event);
    }
}
*/

// Placeholder class to prevent import errors
public class QueueEventConsumer {
    // KAFKA ISOLATED - Event consumption disabled
}
