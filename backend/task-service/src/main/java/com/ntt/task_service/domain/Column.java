package com.ntt.task_service.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

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
@Table(name = "columns")
public class Column {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @jakarta.persistence.Column(name = "project_id")
    String projectId;

    String name;
    Double position;

    @CreationTimestamp
    @jakarta.persistence.Column(name = "created_at")
    Instant createdAt;

    @UpdateTimestamp
    @jakarta.persistence.Column(name = "updated_at")
    Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "column_id")
    @Builder.Default
    List<Task> tasks = new ArrayList<>();
}
