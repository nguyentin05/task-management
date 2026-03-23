package com.ntt.task_service.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.domain.ProjectRole;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, String> {
    boolean existsByProjectIdAndUserIdAndRole(String projectId, String userId, ProjectRole role);

    boolean existsByProjectIdAndUserId(String projectId, String userId);

    Page<ProjectMember> findByProjectId(String projectId, Pageable pageable);

    @Query("SELECT pm.userId FROM ProjectMember pm WHERE pm.project.id = :projectId")
    Set<String> findUserIdsByProjectId(String projectId);

    Optional<ProjectMember> findByProjectIdAndUserId(String projectId, String userId);

    long countByProjectId(String projectId);
}
