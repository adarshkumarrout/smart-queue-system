package com.smartqueue.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QueueEventMessage {
    private String eventType;
    private Long queueId;
    private Long tokenId;
    private Long userId;
    private Long branchId;
    private Long businessId;
    private String tokenNumber;
    private Integer position;
    private Integer waitTimeMinutes;
    private LocalDateTime timestamp;
}
