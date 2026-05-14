package com.smartqueue.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "queues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private QueueStatus status = QueueStatus.ACTIVE;

    @Column(name = "max_capacity")
    @Builder.Default
    private Integer maxCapacity = 100;

    @Column(name = "avg_service_time_minutes")
    @Builder.Default
    private Integer avgServiceTimeMinutes = 5;

    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Token> tokens = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "queue_staff",
        joinColumns = @JoinColumn(name = "queue_id"),
        inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    @Builder.Default
    private List<Staff> assignedStaff = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum QueueStatus {
        ACTIVE, PAUSED, CLOSED
    }
}
