package com.ntt.task_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.Workspace;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    boolean existsByUserId(String userId);

    @Query(
            value = "SELECT p FROM Project p JOIN p.workspaces w WHERE w.id = :workspaceId",
            countQuery = "SELECT count(p) FROM Project p JOIN p.workspaces w WHERE w.id = :workspaceId")
    Page<Project> findProjectsById(String id, Pageable pageable);

    Optional<Workspace> findByUserId(String userId);
}
