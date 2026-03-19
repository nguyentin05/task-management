package com.ntt.task_service.domain;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.persistence.Column;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "column_id")
    String columnId;

    String title;
    String description;
    Double position;

    @Column(name = "start_at")
    Instant startAt;

    @Column(name = "due_at")
    Instant dueAt;

    @Column(name = "created_by")
    String createdBy;

    @Column(name = "created_at")
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "completed_at")
    Instant completedAt;

    @Column(name = "assignee_id")
    String assigneeId;

    @Enumerated(EnumType.STRING)
    TaskLabel label;
}
