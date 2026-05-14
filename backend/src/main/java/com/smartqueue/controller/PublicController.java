package com.smartqueue.controller;

import com.smartqueue.dto.response.ApiResponse;
import com.smartqueue.dto.response.BranchResponse;
import com.smartqueue.dto.response.BusinessResponse;
import com.smartqueue.dto.response.QueueResponse;
import com.smartqueue.model.Branch;
import com.smartqueue.model.Business;
import com.smartqueue.service.BusinessService;
import com.smartqueue.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public read-only endpoints — accessible by any authenticated user (USER, ADMIN, STAFF).
 * Lets regular users browse businesses → branches → queues before joining.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public Browse")
public class PublicController {

    private final BusinessService businessService;
    private final QueueService queueService;

    // ✅ FIXED: Return DTO instead of entity
    @GetMapping("/businesses")
    @Operation(summary = "List all active businesses")
    public ResponseEntity<ApiResponse<List<BusinessResponse>>> getBusinesses() {

        List<BusinessResponse> response = businessService.getAllBusinesses()
                .stream()
                .map(this::toBusinessResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ✅ FIXED: Return DTO instead of entity
    @GetMapping("/businesses/{businessId}/branches")
    @Operation(summary = "List branches of a business")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranches(@PathVariable Long businessId) {

        List<BranchResponse> response = businessService.getBranchesByBusiness(businessId)
                .stream()
                .map(this::toBranchResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/branches/{branchId}/queues")
    @Operation(summary = "List active queues in a branch")
    public ResponseEntity<ApiResponse<List<QueueResponse>>> getQueues(@PathVariable Long branchId) {
        return ResponseEntity.ok(ApiResponse.ok(queueService.getQueuesByBranch(branchId)));
    }

    private BranchResponse toBranchResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .city(branch.getCity())
                .businessId(branch.getBusiness().getId())
                .businessName(branch.getBusiness().getName())
                .isActive(branch.isActive())
                .createdAt(branch.getCreatedAt())
                .build();
    }

    private BusinessResponse toBusinessResponse(Business business) {
        return BusinessResponse.builder()
                .id(business.getId())
                .name(business.getName())
                .description(business.getDescription())
                .ownerEmail(business.getOwnerEmail())
                .isActive(business.isActive())
                .createdAt(business.getCreatedAt())
                .branches(business.getBranches().stream()
                        .map(this::toBranchResponse)
                        .toList())
                .build();
    }
}
