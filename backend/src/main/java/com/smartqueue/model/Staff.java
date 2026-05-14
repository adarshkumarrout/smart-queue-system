package com.smartqueue.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "staff")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "tokens_served_today")
    @Builder.Default
    private Integer tokensServedToday = 0;

    @Column(name = "avg_handling_time_minutes")
    @Builder.Default
    private Double avgHandlingTimeMinutes = 5.0;

    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    private StaffStatus status = StaffStatus.AVAILABLE;

    @ManyToMany(mappedBy = "assignedStaff")
    @Builder.Default
    private List<Queue> assignedQueues = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum StaffStatus {
        AVAILABLE, BUSY, ON_BREAK, OFFLINE
    }
}
