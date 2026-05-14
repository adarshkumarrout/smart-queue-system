package com.smartqueue.controller;

import com.smartqueue.dto.request.CreateBranchRequest;
import com.smartqueue.dto.request.CreateBusinessRequest;
import com.smartqueue.dto.request.CreateQueueRequest;
import com.smartqueue.dto.response.*;
import com.smartqueue.model.*;
import com.smartqueue.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations")
public class AdminController {

    private final BusinessService businessService;
    private final QueueService queueService;
    private final StaffService staffService;
    private final AnalyticsService analyticsService;
    private final TokenService tokenService;

    @PostMapping("/businesses")
    public ResponseEntity<ApiResponse<BusinessResponse>> createBusiness(@Valid @RequestBody CreateBusinessRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(toBusinessResponse(businessService.createBusiness(req))));
    }

    @GetMapping("/businesses")
    public ResponseEntity<ApiResponse<List<BusinessResponse>>> getBusinesses() {
        return ResponseEntity.ok(ApiResponse.ok(
                businessService.getAllBusinesses().stream()
                        .map(this::toBusinessResponse)
                        .toList()
        ));
    }

    @PostMapping("/branches")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody CreateBranchRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(toBranchResponse(businessService.createBranch(req))));
    }

    @GetMapping("/businesses/{businessId}/branches")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranches(@PathVariable Long businessId) {
        return ResponseEntity.ok(ApiResponse.ok(
                businessService.getBranchesByBusiness(businessId).stream()
                        .map(this::toBranchResponse)
                        .toList()
        ));
    }

    @PostMapping("/queues")
    @Operation(summary = "Create a queue")
    public ResponseEntity<ApiResponse<QueueResponse>> createQueue(@Valid @RequestBody CreateQueueRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(queueService.createQueue(req)));
    }

    @PatchMapping("/queues/{queueId}/status")
    public ResponseEntity<ApiResponse<QueueResponse>> updateQueueStatus(
            @PathVariable Long queueId, @RequestParam Queue.QueueStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(queueService.updateStatus(queueId, status)));
    }

    @PostMapping("/staff/{userId}/branch/{branchId}")
    @Operation(summary = "Add staff member")
    public ResponseEntity<ApiResponse<StaffResponse>> addStaff(
            @PathVariable Long userId, @PathVariable Long branchId) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.addStaff(userId, branchId)));
    }

    @PostMapping("/staff/{staffId}/assign-queue/{queueId}")
    @Operation(summary = "Assign staff to queue")
    public ResponseEntity<ApiResponse<StaffResponse>> assignStaff(
            @PathVariable Long staffId, @PathVariable Long queueId) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.assignToQueue(staffId, queueId)));
    }

    @GetMapping("/branches/{branchId}/staff")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getStaff(@PathVariable Long branchId) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getStaffByBranch(branchId)));
    }

    @GetMapping("/queues/{queueId}/analytics")
    @Operation(summary = "Get queue analytics")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(@PathVariable Long queueId) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getQueueAnalytics(queueId)));
    }

    @PostMapping("/tokens/{tokenId}/no-show")
    public ResponseEntity<ApiResponse<TokenResponse>> markNoShow(@PathVariable Long tokenId) {
        return ResponseEntity.ok(ApiResponse.ok(tokenService.markNoShow(tokenId)));
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
