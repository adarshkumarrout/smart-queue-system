package com.smartqueue.repository;

import com.smartqueue.model.QueueEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QueueEventRepository extends MongoRepository<QueueEvent, String> {
    List<QueueEvent> findByQueueIdOrderByTimestampDesc(Long queueId);
    List<QueueEvent> findByEventTypeAndTimestampAfter(String eventType, LocalDateTime after);
    List<QueueEvent> findByBusinessIdAndTimestampBetween(Long businessId, LocalDateTime start, LocalDateTime end);
    long countByQueueIdAndEventType(Long queueId, String eventType);
}
