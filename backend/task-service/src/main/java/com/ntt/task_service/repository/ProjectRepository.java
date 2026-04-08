package com.ntt.task_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ntt.task_service.domain.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query(
            value = "SELECT p FROM Project p JOIN p.members m WHERE m.userId = :userId",
            countQuery = "SELECT count(p) FROM Project p JOIN p.members m WHERE m.userId = :userId")
    Page<Project> findProjectsByUserId(String userId, Pageable pageable);
}
