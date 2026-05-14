package com.smartqueue.repository;

import com.smartqueue.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    List<Queue> findByBranchId(Long branchId);
    List<Queue> findByBranchIdAndStatus(Long branchId, Queue.QueueStatus status);

    @Query("SELECT q FROM Queue q JOIN q.branch b WHERE b.business.id = :businessId")
    List<Queue> findByBusinessId(Long businessId);
}
