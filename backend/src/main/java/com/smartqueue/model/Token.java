package com.smartqueue.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_number", nullable = false)
    private String tokenNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TokenStatus status = TokenStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_type")
    @Builder.Default
    private PriorityType priorityType = PriorityType.NORMAL;

    @Column(name = "position_in_queue")
    private Integer positionInQueue;

    @Column(name = "estimated_wait_minutes")
    private Integer estimatedWaitMinutes;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "served_at")
    private LocalDateTime servedAt;

    @Column(name = "no_show_at")
    private LocalDateTime noShowAt;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum TokenStatus {
        WAITING, CALLED, SERVING, SERVED, NO_SHOW, CANCELLED
    }

    public enum PriorityType {
        NORMAL, VIP, EMERGENCY
    }
}
