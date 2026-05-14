package com.smartqueue.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_POSITION_KEY  = "queue:%d:position:%d";
    private static final String QUEUE_WAIT_TIME_KEY = "queue:%d:waittime";
    private static final String QUEUE_SIZE_KEY      = "queue:%d:size";
    private static final String RATE_LIMIT_KEY      = "ratelimit:user:%d:joins";
    private static final Duration TTL               = Duration.ofMinutes(30);
    private static final Duration RATE_TTL          = Duration.ofHours(1);

    public void setQueuePosition(Long queueId, Long userId, int position) {
        String key = String.format(QUEUE_POSITION_KEY, queueId, userId);
        redisTemplate.opsForValue().set(key, position, TTL);
    }

    public Optional<Integer> getQueuePosition(Long queueId, Long userId) {
        String key = String.format(QUEUE_POSITION_KEY, queueId, userId);
        Object val = redisTemplate.opsForValue().get(key);
        return val != null ? Optional.of((Integer) val) : Optional.empty();
    }

    public void setQueueWaitTime(Long queueId, int waitMinutes) {
        String key = String.format(QUEUE_WAIT_TIME_KEY, queueId);
        redisTemplate.opsForValue().set(key, waitMinutes, TTL);
    }

    public Optional<Integer> getQueueWaitTime(Long queueId) {
        String key = String.format(QUEUE_WAIT_TIME_KEY, queueId);
        Object val = redisTemplate.opsForValue().get(key);
        return val != null ? Optional.of((Integer) val) : Optional.empty();
    }

    public void setQueueSize(Long queueId, long size) {
        String key = String.format(QUEUE_SIZE_KEY, queueId);
        redisTemplate.opsForValue().set(key, size, TTL);
    }

    public Optional<Long> getQueueSize(Long queueId) {
        String key = String.format(QUEUE_SIZE_KEY, queueId);
        Object val = redisTemplate.opsForValue().get(key);
        return val != null ? Optional.of(((Number) val).longValue()) : Optional.empty();
    }

    public void invalidateQueue(Long queueId) {
        redisTemplate.delete(String.format(QUEUE_WAIT_TIME_KEY, queueId));
        redisTemplate.delete(String.format(QUEUE_SIZE_KEY, queueId));
    }

    public void invalidateUserPosition(Long queueId, Long userId) {
        redisTemplate.delete(String.format(QUEUE_POSITION_KEY, queueId, userId));
    }

    public boolean isRateLimited(Long userId, int maxJoins) {
        String key = String.format(RATE_LIMIT_KEY, userId);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) redisTemplate.expire(key, RATE_TTL);
        return count > maxJoins;
    }
}
