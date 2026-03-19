package com.ntt.task_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntt.task_service.domain.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    long countByColumnIdIn(List<String> columnIds);

    long countByColumnIdInAndCompletedAtIsNotNull(List<String> columnIds);
}
