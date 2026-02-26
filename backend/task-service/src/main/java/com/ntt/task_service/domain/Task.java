package com.ntt.task_service.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@Entity
//@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String columnId;
    String title;
    String description;
    long position;
    Instant startAt;
    Instant dueAt;
    String createdBy;
    Instant createdAt;
    Instant updatedAt;
    Instant completedAt;

}
