package com.smartqueue.repository;

import com.smartqueue.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByBranchId(Long branchId);
    Optional<Staff> findByUserId(Long userId);
    List<Staff> findByBranchIdAndStatus(Long branchId, Staff.StaffStatus status);

    @Query("SELECT COUNT(s) FROM Staff s JOIN s.assignedQueues q WHERE q.id = :queueId AND s.status = 'AVAILABLE'")
    long countActiveStaffByQueueId(Long queueId);
}
