package com.smartqueue.kafka;

/*
 * KAFKA ISOLATED - This Kafka producer has been commented out
 * The application now works without publishing queue events to Kafka.
 * Real-time updates still work through WebSockets and Redis caching.
 */

/*
import com.smartqueue.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueEventProducer {

    private final KafkaTemplate<String, QueueEventMessage> kafkaTemplate;

    public void publishJoinEvent(QueueEventMessage message) {
        kafkaTemplate.send(KafkaConfig.TOPIC_QUEUE_JOIN, 
            String.valueOf(message.getQueueId()), message);
        log.info("Published join event for token {} in queue {}", 
            message.getTokenNumber(), message.getQueueId());
    }

    public void publishServedEvent(QueueEventMessage message) {
        kafkaTemplate.send(KafkaConfig.TOPIC_QUEUE_SERVED,
            String.valueOf(message.getQueueId()), message);
        log.info("Published served event for token {}", message.getTokenNumber());
    }

    public void publishNoShowEvent(QueueEventMessage message) {
        kafkaTemplate.send(KafkaConfig.TOPIC_QUEUE_NO_SHOW,
            String.valueOf(message.getQueueId()), message);
        log.info("Published no-show event for token {}", message.getTokenNumber());
    }
}
*/

// Placeholder class to prevent import errors
public class QueueEventProducer {
    public void publishJoinEvent(QueueEventMessage message) {
        // KAFKA ISOLATED - Event publishing disabled
    }

    public void publishServedEvent(QueueEventMessage message) {
        // KAFKA ISOLATED - Event publishing disabled
    }

    public void publishNoShowEvent(QueueEventMessage message) {
        // KAFKA ISOLATED - Event publishing disabled
    }
}
