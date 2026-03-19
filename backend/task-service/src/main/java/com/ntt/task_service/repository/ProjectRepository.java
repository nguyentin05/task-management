package com.ntt.task_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntt.task_service.domain.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {}
