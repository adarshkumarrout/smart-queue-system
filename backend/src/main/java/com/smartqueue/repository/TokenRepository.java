package com.smartqueue.repository;

import com.smartqueue.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByQueueIdAndStatusOrderByPriorityTypeDescCreatedAtAsc(
        Long queueId, Token.TokenStatus status);

    List<Token> findByQueueIdAndStatus(Long queueId, Token.TokenStatus status);

    Optional<Token> findByQueueIdAndUserId(Long queueId, Long userId);

    @Query("SELECT COUNT(t) FROM Token t WHERE t.queue.id = :queueId AND t.status = 'WAITING'")
    long countWaitingByQueueId(Long queueId);

    @Query("SELECT t FROM Token t WHERE t.status = 'WAITING' AND t.joinedAt < :timeout")
    List<Token> findNoShowCandidates(LocalDateTime timeout);

    List<Token> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COUNT(t) FROM Token t WHERE t.queue.id = :queueId AND t.status = 'WAITING' AND t.priorityType = :priority")
    long countByQueueAndPriority(Long queueId, Token.PriorityType priority);
}
