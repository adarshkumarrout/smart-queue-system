package com.smartqueue.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data @Builder
public class AnalyticsResponse {
    private Long queueId;
    private String queueName;
    private long totalServed;
    private long totalNoShows;
    private long currentWaiting;
    private double avgWaitTime;
    private Map<String, Long> hourlyDistribution;
}
