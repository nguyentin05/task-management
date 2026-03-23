package com.ntt.task_service.domain;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "outbox_events",
        indexes = {@Index(name = "index_outbox_events_polling", columnList = "status, retry_count, created_at")})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @jakarta.persistence.Column(nullable = false)
    String routingKey;

    @jakarta.persistence.Column(nullable = false, columnDefinition = "TEXT")
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
