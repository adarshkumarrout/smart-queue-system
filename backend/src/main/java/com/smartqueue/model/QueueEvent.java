package com.smartqueue.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "queue_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QueueEvent {

    @Id
    private String id;

    @Field("event_type")
    private String eventType;

    @Field("queue_id")
    private Long queueId;

    @Field("token_id")
    private Long tokenId;

    @Field("user_id")
    private Long userId;

    @Field("branch_id")
    private Long branchId;

    @Field("business_id")
    private Long businessId;

    @Field("token_number")
    private String tokenNumber;

    @Field("position")
    private Integer position;

    @Field("wait_time_minutes")
    private Integer waitTimeMinutes;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
