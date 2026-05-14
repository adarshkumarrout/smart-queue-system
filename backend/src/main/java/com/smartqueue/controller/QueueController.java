package com.smartqueue.controller;

import com.smartqueue.dto.request.JoinQueueRequest;
import com.smartqueue.dto.response.ApiResponse;
import com.smartqueue.dto.response.QueueResponse;
import com.smartqueue.dto.response.TokenResponse;
import com.smartqueue.model.Queue;
import com.smartqueue.model.Token;
import com.smartqueue.repository.UserRepository;
import com.smartqueue.service.QueueService;
import com.smartqueue.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
@Tag(name = "Queue Operations")
public class QueueController {

    private final QueueService queueService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Get queues by branch")
    public ResponseEntity<ApiResponse<List<QueueResponse>>> getQueuesByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(ApiResponse.ok(queueService.getQueuesByBranch(branchId)));
    }

    @GetMapping("/{queueId}")
    @Operation(summary = "Get queue snapshot")
    public ResponseEntity<ApiResponse<QueueResponse>> getQueue(@PathVariable Long queueId) {
        return ResponseEntity.ok(ApiResponse.ok(queueService.getQueueSnapshot(queueId)));
    }

    @PostMapping("/join")
    @Operation(summary = "Join a queue")
    public ResponseEntity<ApiResponse<TokenResponse>> joinQueue(
            @RequestBody JoinQueueRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(ApiResponse.ok("Joined queue", tokenService.joinQueue(userId, request)));
    }

    @GetMapping("/{queueId}/tokens")
    @Operation(summary = "Get tokens in queue")
    public ResponseEntity<ApiResponse<List<TokenResponse>>> getTokens(
            @PathVariable Long queueId,
            @RequestParam(required = false) Token.TokenStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(tokenService.getQueueTokens(queueId, status)));
    }

    @PostMapping("/{queueId}/serve-next")
    @Operation(summary = "Serve next token in queue")
    public ResponseEntity<ApiResponse<TokenResponse>> serveNext(@PathVariable Long queueId) {
        return ResponseEntity.ok(ApiResponse.ok("Token served", tokenService.serveNextToken(queueId)));
    }

    @GetMapping("/my-tokens")
    @Operation(summary = "Get current user tokens")
    public ResponseEntity<ApiResponse<List<TokenResponse>>> myTokens(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(ApiResponse.ok(tokenService.getUserTokens(userId)));
    }
}
