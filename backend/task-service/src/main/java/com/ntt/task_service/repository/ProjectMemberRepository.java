package com.ntt.task_service.repository;

import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.domain.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, String> {
    boolean existsByProjectIdAndUserIdAndRole(String projectId, String userId, ProjectRole role);

    boolean existsByProjectIdAndUserId(String projectId, String userId);

    List<ProjectMember> findByProjectId(String projectId);
    Set<String> findUserIdsByProjectId(String projectId);
    Optional<ProjectMember> findByProjectIdAndUserId(String projectId, String userId);
}
