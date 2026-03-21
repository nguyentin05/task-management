package com.ntt.task_service.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "created_by")
    String createdBy;

    @Column(name = "start_at")
    Instant startAt;

    @Column(name = "end_at")
    Instant endAt;

    @CreationTimestamp
    @Column(name = "created_at")
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "workspace_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "workspace_id"),
            indexes = {
                @Index(name = "index_workspace_project_project_id", columnList = "project_id"),
                @Index(name = "index_workspace_project_workspace_id", columnList = "workspace_id")
            })
    @Builder.Default
    Set<Workspace> workspaces = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<com.ntt.task_service.domain.Column> columns = new ArrayList<>();
}
