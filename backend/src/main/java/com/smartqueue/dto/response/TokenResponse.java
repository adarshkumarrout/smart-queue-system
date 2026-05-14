package com.smartqueue.dto.response;

import com.smartqueue.model.Token;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class TokenResponse {
    private Long id;
    private String tokenNumber;
    private Long queueId;
    private String queueName;
    private Long userId;
    private String username;
    private Token.TokenStatus status;
    private Token.PriorityType priorityType;
    private Integer positionInQueue;
    private Integer estimatedWaitMinutes;
    private LocalDateTime joinedAt;
    private LocalDateTime servedAt;
}
