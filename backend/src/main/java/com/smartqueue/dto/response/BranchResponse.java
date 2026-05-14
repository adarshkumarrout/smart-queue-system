package com.smartqueue.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class BranchResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Long businessId;
    private String businessName;
    private boolean isActive;
    private LocalDateTime createdAt;
}