package com.ntt.task_service.domain;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
        name = "workspaces",
        indexes = {@Index(name = "index_workspaces_user_id", columnList = "user_id")})
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "user_id", unique = true, nullable = false)
    String userId;

    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @CreationTimestamp
    @Column(name = "created_at")
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @ManyToMany(mappedBy = "workspaces", fetch = FetchType.LAZY)
    @Builder.Default
    Set<Project> projects = new HashSet<>();
}
