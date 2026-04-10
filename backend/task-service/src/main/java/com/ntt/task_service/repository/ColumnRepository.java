package com.ntt.task_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ntt.task_service.domain.Column;

@Repository
public interface ColumnRepository extends JpaRepository<Column, String> {
    @Query("SELECT c.id FROM Column c WHERE c.projectId = :projectId ORDER BY c.position ASC")
    Page<String> findIdsByProjectId(@Param("projectId") String projectId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Column c LEFT JOIN FETCH c.tasks WHERE c.id IN :ids ORDER BY c.position ASC")
    List<Column> findByIdsWithTasks(@Param("ids") List<String> ids);

    @Query("SELECT c.id FROM Column c WHERE c.projectId = :projectId")
    List<String> findColumnIdsByProjectId(@Param("projectId") String projectId);

    @Query("SELECT c.id FROM Column c WHERE c.projectId = :projectId AND c.isDoneColumn = true")
    Optional<String> findDoneColumnIdByProjectId(@Param("projectId") String projectId);
}
