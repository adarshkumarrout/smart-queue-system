package com.smartqueue.dto.response;

import com.smartqueue.model.Staff;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class StaffResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String username;
    private Long branchId;
    private String branchName;
    private Staff.StaffStatus status;
    private Integer tokensServedToday;
    private Double avgHandlingTimeMinutes;
}
