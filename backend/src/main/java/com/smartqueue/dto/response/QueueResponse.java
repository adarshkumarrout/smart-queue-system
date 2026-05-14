package com.smartqueue.dto.response;

import com.smartqueue.model.Queue;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class QueueResponse {
    private Long id;
    private String name;
    private String description;
    private Long branchId;
    private String branchName;
    private Queue.QueueStatus status;
    private Integer maxCapacity;
    private Integer avgServiceTimeMinutes;
    private long waitingCount;
    private Integer estimatedWaitMinutes;
    private int activeStaff;
}
