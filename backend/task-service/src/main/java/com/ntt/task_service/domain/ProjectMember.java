package com.ntt.task_service.domain;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

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
        name = "project_member",
        indexes = {@Index(name = "index_project_member_project_id_user_id", columnList = "project_id, user_id")})
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProjectRole role;

    @CreationTimestamp
    @Column(name = "created_at")
    Instant createdAt;
}
