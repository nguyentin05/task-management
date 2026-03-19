package com.ntt.task_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.task_service.domain.Column;
import com.ntt.task_service.dto.request.ColumnCreationRequest;
import com.ntt.task_service.dto.request.ColumnUpdateRequest;
import com.ntt.task_service.dto.response.ColumnResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ColumnMapper;
import com.ntt.task_service.repository.ColumnRepository;
import com.ntt.task_service.repository.ProjectRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ColumnService {
    ColumnRepository columnRepository;
    ProjectRepository projectRepository;
    ColumnMapper columnMapper;
    ProjectAuthorizationService projectAuthorizationService;

    public ColumnResponse createColumnInProject(String id, ColumnCreationRequest request) {
        checkProject(id);

        projectAuthorizationService.validateCanManage(id);

        Column column = columnMapper.toColumn(request);

        column.setProjectId(id);

        return columnMapper.toColumnResponse(columnRepository.save(column));
    }

    public List<ColumnResponse> getAllColumnInProject(String id) {
        checkProject(id);

        projectAuthorizationService.validateCanView(id);

        return columnRepository.findByProjectIdWithTasks(id).stream()
                .map(columnMapper::toColumnResponse)
                .toList();
    }

    public ColumnResponse updateColumnInProject(String projectId, String columnId, ColumnUpdateRequest request) {
        checkProject(projectId);

        Column column =
                columnRepository.findById(columnId).orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        if (!column.getProjectId().equals(projectId)) throw new AppException(ErrorCode.COLUMN_NOT_IN_PROJECT);

        projectAuthorizationService.validateCanManage(projectId);

        columnMapper.updateColumn(column, request);

        return columnMapper.toColumnResponse(columnRepository.save(column));
    }

    @Transactional
    public void deleteColumnInProject(String projectId, String columnId) {
        checkProject(projectId);

        Column column =
                columnRepository.findById(columnId).orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        if (!column.getProjectId().equals(projectId)) throw new AppException(ErrorCode.COLUMN_NOT_IN_PROJECT);

        projectAuthorizationService.validateCanManage(projectId);

        columnRepository.delete(column);
    }

    private void checkProject(String id) {
        if (!projectRepository.existsById(id)) throw new AppException(ErrorCode.PROJECT_NOT_FOUND);
    }
}
