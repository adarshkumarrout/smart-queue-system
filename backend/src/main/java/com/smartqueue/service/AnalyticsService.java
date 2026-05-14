package com.smartqueue.service;

import com.smartqueue.dto.response.AnalyticsResponse;
import com.smartqueue.exception.ResourceNotFoundException;
import com.smartqueue.model.Queue;
import com.smartqueue.repository.QueueEventRepository;
import com.smartqueue.repository.QueueRepository;
import com.smartqueue.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final QueueRepository queueRepository;
    private final TokenRepository tokenRepository;
    private final QueueEventRepository eventRepository;

    public AnalyticsResponse getQueueAnalytics(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));

        long served  = eventRepository.countByQueueIdAndEventType(queueId, "QUEUE_SERVED");
        long noShows = eventRepository.countByQueueIdAndEventType(queueId, "QUEUE_NO_SHOW");
        long waiting = tokenRepository.countWaitingByQueueId(queueId);

        return AnalyticsResponse.builder()
                .queueId(queueId)
                .queueName(queue.getName())
                .totalServed(served)
                .totalNoShows(noShows)
                .currentWaiting(waiting)
                .avgWaitTime(queue.getAvgServiceTimeMinutes())
                .hourlyDistribution(new HashMap<>())
                .build();
    }
}
