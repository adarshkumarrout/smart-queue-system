package com.smartqueue.config;

/*
 * KAFKA ISOLATED - This Kafka configuration has been commented out
 * The application now works without event streaming via Kafka.
 * Real-time updates still work through WebSockets and Redis caching.
 */

/*
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_QUEUE_JOIN    = "queue.join";
    public static final String TOPIC_QUEUE_SERVED  = "queue.served";
    public static final String TOPIC_QUEUE_NO_SHOW = "queue.no_show";

    @Bean public NewTopic queueJoinTopic()   { return TopicBuilder.name(TOPIC_QUEUE_JOIN).partitions(3).replicas(1).build(); }
    @Bean public NewTopic queueServedTopic() { return TopicBuilder.name(TOPIC_QUEUE_SERVED).partitions(3).replicas(1).build(); }
    @Bean public NewTopic queueNoShowTopic() { return TopicBuilder.name(TOPIC_QUEUE_NO_SHOW).partitions(3).replicas(1).build(); }
}
*/

// Placeholder class to prevent import errors
public class KafkaConfig {
    public static final String TOPIC_QUEUE_JOIN    = "queue.join";
    public static final String TOPIC_QUEUE_SERVED  = "queue.served";
    public static final String TOPIC_QUEUE_NO_SHOW = "queue.no_show";
}
