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
//@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String workspaceId;
    String name;
    String description;
    ProjectStatus projectStatus;
    String createdBy;
    Instant startAt;
    Instant endAt;
    Instant createdAt;
    Instant updatedAt;
}
