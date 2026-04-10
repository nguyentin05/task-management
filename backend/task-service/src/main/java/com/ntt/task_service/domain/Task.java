package com.ntt.task_service.domain;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "tasks",
        indexes = {
            @Index(name = "index_tasks_column_id", columnList = "column_id"),
            @Index(name = "index_tasks_position", columnList = "column_id, position"),
            @Index(name = "index_tasks_assignee_id", columnList = "assignee_id"),
        })
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "column_id")
    String columnId;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false)
    Double position;

    @Column(name = "start_at")
    Instant startAt;

    @Column(name = "due_at")
    Instant dueAt;

    @Column(name = "created_by")
    String createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "assignee_id")
    String assigneeId;

    @Enumerated(EnumType.STRING)
    TaskLabel label;
}
