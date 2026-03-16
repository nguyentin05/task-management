package com.ntt.task_service.repository;

import com.ntt.task_service.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    long countByColumnIdIn(List<String> columnIds);
    long countByColumnIdInAndCompletedAtIsNotNull(List<String> columnIds);
}
