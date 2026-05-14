package com.smartqueue.service;

import com.smartqueue.cache.RedisCacheService;
import com.smartqueue.dto.request.CreateQueueRequest;
import com.smartqueue.dto.response.QueueResponse;
import com.smartqueue.exception.ResourceNotFoundException;
import com.smartqueue.model.Queue;
import com.smartqueue.model.Branch;
import com.smartqueue.repository.*;
import com.smartqueue.util.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final BranchRepository branchRepository;
    private final TokenRepository tokenRepository;
    private final StaffRepository staffRepository;
    private final RedisCacheService cacheService;
    private final PredictionService predictionService;

    @Transactional
    public QueueResponse createQueue(CreateQueueRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        Queue queue = Queue.builder()
                .name(request.getName())
                .description(request.getDescription())
                .branch(branch)
                .maxCapacity(request.getMaxCapacity())
                .avgServiceTimeMinutes(request.getAvgServiceTimeMinutes())
                .build();
        queue = queueRepository.save(queue);
        return toResponse(queue);
    }

    public List<QueueResponse> getQueuesByBranch(Long branchId) {
        return queueRepository.findByBranchId(branchId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public QueueResponse getQueueSnapshot(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        return toResponse(queue);
    }

    @Transactional
    public QueueResponse updateStatus(Long queueId, Queue.QueueStatus status) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        queue.setStatus(status);
        cacheService.invalidateQueue(queueId);
        return toResponse(queueRepository.save(queue));
    }

    public QueueResponse toResponse(Queue queue) {
        long waiting = cacheService.getQueueSize(queue.getId())
                .orElseGet(() -> tokenRepository.countWaitingByQueueId(queue.getId()));
        int waitTime = cacheService.getQueueWaitTime(queue.getId())
                .orElseGet(() -> predictionService.estimateWaitTime(queue, waiting));
        int activeStaff = (int) staffRepository.countActiveStaffByQueueId(queue.getId());

        return QueueResponse.builder()
                .id(queue.getId())
                .name(queue.getName())
                .description(queue.getDescription())
                .branchId(queue.getBranch().getId())
                .branchName(queue.getBranch().getName())
                .status(queue.getStatus())
                .maxCapacity(queue.getMaxCapacity())
                .avgServiceTimeMinutes(queue.getAvgServiceTimeMinutes())
                .waitingCount(waiting)
                .estimatedWaitMinutes(waitTime)
                .activeStaff(activeStaff)
                .build();
    }
}
