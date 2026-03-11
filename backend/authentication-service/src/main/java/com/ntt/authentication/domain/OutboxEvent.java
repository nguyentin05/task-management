package com.ntt.authentication.domain;

import java.time.Instant;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String routingKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OutboxStatus status;

    @CreationTimestamp
    Instant createdAt;

    int retryCount;

    public enum OutboxStatus {
        PENDING,
        FAILED
    }
}
