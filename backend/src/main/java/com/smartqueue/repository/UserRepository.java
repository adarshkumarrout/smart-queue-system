package com.smartqueue.repository;

import com.smartqueue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.business WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.business WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
