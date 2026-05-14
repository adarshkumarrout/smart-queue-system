package com.smartqueue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQueueRequest {
    @NotBlank private String name;
    private String description;
    @NotNull private Long branchId;
    private Integer maxCapacity = 100;
    private Integer avgServiceTimeMinutes = 5;
}
