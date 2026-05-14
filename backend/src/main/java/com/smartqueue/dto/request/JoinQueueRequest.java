package com.smartqueue.dto.request;

import com.smartqueue.model.Token;
import lombok.Data;

@Data
public class JoinQueueRequest {
    private Long queueId;
    private Token.PriorityType priorityType = Token.PriorityType.NORMAL;
    private String notes;
}
