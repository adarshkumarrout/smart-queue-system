package com.smartqueue.repository;

import com.smartqueue.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    boolean existsByName(String name);
}
