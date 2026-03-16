package com.ntt.task_service.repository;

import com.ntt.task_service.domain.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<Column, String> {
    @Query("SELECT DISTINCT c FROM Column c LEFT JOIN FETCH c.tasks " +
            "WHERE c.projectId = :projectId ORDER BY c.position")
    List<Column> findByProjectIdWithTasks(@Param("projectId") String projectId);

    @Query("SELECT c.id FROM Column c WHERE c.projectId = :projectId")
    List<String> findColumnIdsByProjectId(@Param("projectId") String projectId);
}
