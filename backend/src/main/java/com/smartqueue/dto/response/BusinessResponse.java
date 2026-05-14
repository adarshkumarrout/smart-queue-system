package com.smartqueue.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class BusinessResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerEmail;
    private boolean isActive;
    private LocalDateTime createdAt;
    private List<BranchResponse> branches; // safe — no back-reference
}
