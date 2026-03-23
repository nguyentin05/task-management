package com.ntt.task_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;

import com.ntt.task_service.domain.Column;
import com.ntt.task_service.dto.request.ColumnCreationRequest;
import com.ntt.task_service.dto.request.ColumnUpdateRequest;
import com.ntt.task_service.dto.response.ColumnResponse;
import com.ntt.task_service.dto.response.PageResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ColumnMapper;
import com.ntt.task_service.repository.ColumnRepository;
import com.ntt.task_service.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ColumnServiceTest {

    @Mock
    ColumnRepository columnRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ColumnMapper columnMapper;

    @Mock
    ProjectAuthorizationService projectAuthorizationService;

    @InjectMocks
    ColumnService columnService;

    private static final String PROJECT_ID = "project-uuid-1234";
    private static final String COLUMN_ID = "column-uuid-1234";

    private Column column;

    @Test
    @DisplayName("Success: project không có column nào, trả về danh sách rỗng")
    void findColumnIdsByProjectId_NoColumns_ShouldReturnEmptyList() {
        List<String> result = columnRepository.findColumnIdsByProjectId("non-existing-project");

        assertThat(result).isEmpty();
    }

    @BeforeEach
    void setUpGlobal() {
        column = Column.builder()
                .id(COLUMN_ID)
                .projectId(PROJECT_ID)
                .name("To Do")
                .position(1.0)
                .build();
    }

    @Nested
    @DisplayName("Scenario - Fail: project không tồn tại")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class ProjectNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(projectRepository.existsById(anyString())).thenReturn(false);
        }

        @Test
        @DisplayName("Create Column: trả về lỗi PROJECT_NOT_FOUND")
        void createColumnTest() {
            assertThatThrownBy(() -> columnService.createColumnInProject(
                            PROJECT_ID,
                            ColumnCreationRequest.builder()
                                    .name("To Do")
                                    .position(1.0)
                                    .build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);

            verify(columnRepository, never()).save(any());
        }

        @Test
        @DisplayName("Get All Column: trả về lỗi PROJECT_NOT_FOUND")
        void getAllColumnTest() {
            assertThatThrownBy(() -> columnService.getAllColumnInProject(PROJECT_ID, 1, 10))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }

        @Test
        @DisplayName("Update Column: trả về lỗi PROJECT_NOT_FOUND")
        void updateColumnTest() {
            assertThatThrownBy(() -> columnService.updateColumnInProject(
                            PROJECT_ID,
                            COLUMN_ID,
                            ColumnUpdateRequest.builder().name("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);

            verify(columnRepository, never()).save(any());
        }

        @Test
        @DisplayName("Delete Column: trả về lỗi PROJECT_NOT_FOUND")
        void deleteColumnTest() {
            assertThatThrownBy(() -> columnService.deleteColumnInProject(PROJECT_ID, COLUMN_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);

            verify(columnRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Scenario - Fail: column không tồn tại")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class ColumnNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(projectRepository.existsById(anyString())).thenReturn(true);
            when(columnRepository.findById(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Update Column: trả về lỗi COLUMN_NOT_FOUND")
        void updateColumnTest() {
            assertThatThrownBy(() -> columnService.updateColumnInProject(
                            PROJECT_ID,
                            COLUMN_ID,
                            ColumnUpdateRequest.builder().name("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(columnRepository, never()).save(any());
        }

        @Test
        @DisplayName("Delete Column: trả về lỗi COLUMN_NOT_FOUND")
        void deleteColumnTest() {
            assertThatThrownBy(() -> columnService.deleteColumnInProject(PROJECT_ID, COLUMN_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(columnRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Create Column In Project: test hàm createColumnInProject")
    class CreateColumnInProjectTest {

        @Test
        @DisplayName("Success: tạo column thành công, trả về ColumnResponse")
        void createColumnInProject_ValidRequest_ShouldReturnColumnResponse() {
            ColumnCreationRequest request =
                    ColumnCreationRequest.builder().name("To Do").position(1.0).build();

            ColumnResponse mockResponse = ColumnResponse.builder()
                    .id(COLUMN_ID)
                    .projectId(PROJECT_ID)
                    .name("To Do")
                    .position(1.0)
                    .build();

            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(columnMapper.toColumn(request)).thenReturn(column);
            when(columnRepository.save(any(Column.class))).thenReturn(column);
            when(columnMapper.toColumnResponse(column)).thenReturn(mockResponse);

            ColumnResponse response = columnService.createColumnInProject(PROJECT_ID, request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(COLUMN_ID);
            assertThat(response.getName()).isEqualTo("To Do");

            verify(columnRepository, times(1)).save(any(Column.class));
        }

        @Test
        @DisplayName("Fail: không có quyền quản lý, trả về lỗi ACCESS_DENIED")
        void createColumnInProject_AccessDenied_ShouldThrow() {
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> columnService.createColumnInProject(
                            PROJECT_ID,
                            ColumnCreationRequest.builder()
                                    .name("To Do")
                                    .position(1.0)
                                    .build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(columnRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get All Column In Project: test hàm getAllColumnInProject phân trang")
    class GetAllColumnInProjectTest {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("position").ascending());

        @Test
        @DisplayName("Success: lấy danh sách column thành công, trả về PageResponse")
        void getAllColumnInProject_ShouldReturnPageResponse() {
            String colId1 = "col-uuid-1";
            String colId2 = "col-uuid-2";
            List<String> columnIds = List.of(colId1, colId2);

            Page<String> pageResult = new PageImpl<>(columnIds, pageable, 2);

            Column col1 = Column.builder()
                    .id(colId1)
                    .projectId(PROJECT_ID)
                    .name("To Do")
                    .build();
            Column col2 = Column.builder()
                    .id(colId2)
                    .projectId(PROJECT_ID)
                    .name("Done")
                    .build();

            ColumnResponse res1 =
                    ColumnResponse.builder().id(colId1).name("To Do").build();
            ColumnResponse res2 =
                    ColumnResponse.builder().id(colId2).name("Done").build();

            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);

            when(columnRepository.findIdsByProjectId(eq(PROJECT_ID), any(Pageable.class)))
                    .thenReturn(pageResult);
            when(columnRepository.findByIdsWithTasks(columnIds)).thenReturn(List.of(col1, col2));

            when(columnMapper.toColumnResponse(col1)).thenReturn(res1);
            when(columnMapper.toColumnResponse(col2)).thenReturn(res2);

            PageResponse<ColumnResponse> result = columnService.getAllColumnInProject(PROJECT_ID, page, size);

            assertThat(result.getCurrentPage()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(1);

            assertThat(result.getData()).hasSize(2);
            assertThat(result.getData().getFirst().getName()).isEqualTo("To Do");

            verify(projectAuthorizationService, times(1)).validateCanView(PROJECT_ID);
        }

        @Test
        @DisplayName("Success: project không có column nào, trả về data rỗng và KHÔNG query lần 2")
        void getAllColumnInProject_Empty_ShouldReturnEmptyPage() {
            Page<String> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(columnRepository.findIdsByProjectId(eq(PROJECT_ID), any(Pageable.class)))
                    .thenReturn(emptyPage);

            PageResponse<ColumnResponse> result = columnService.getAllColumnInProject(PROJECT_ID, page, size);

            assertThat(result.getData()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            verify(columnRepository, never()).findByIdsWithTasks(anyList());
        }

        @Test
        @DisplayName("Fail: không có quyền xem, trả về lỗi ACCESS_DENIED")
        void getAllColumnInProject_AccessDenied_ShouldThrow() {
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanView(PROJECT_ID);

            assertThatThrownBy(() -> columnService.getAllColumnInProject(PROJECT_ID, page, size))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("Update Column In Project: test hàm updateColumnInProject")
    class UpdateColumnInProjectTest {

        @Test
        @DisplayName("Success: cập nhật column thành công, trả về ColumnResponse")
        void updateColumnInProject_ValidRequest_ShouldReturnColumnResponse() {
            ColumnUpdateRequest request =
                    ColumnUpdateRequest.builder().name("In Progress").build();

            ColumnResponse mockResponse =
                    ColumnResponse.builder().id(COLUMN_ID).name("In Progress").build();

            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(columnRepository.save(any(Column.class))).thenReturn(column);
            when(columnMapper.toColumnResponse(column)).thenReturn(mockResponse);

            ColumnResponse response = columnService.updateColumnInProject(PROJECT_ID, COLUMN_ID, request);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("In Progress");

            verify(columnMapper, times(1)).updateColumn(eq(column), eq(request));
            verify(columnRepository, times(1)).save(any(Column.class));
        }

        @Test
        @DisplayName("Fail: column không thuộc project này, trả về lỗi COLUMN_NOT_IN_PROJECT")
        void updateColumnInProject_ColumnNotInProject_ShouldThrow() {
            column.setProjectId("other-project-uuid");

            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));

            assertThatThrownBy(() -> columnService.updateColumnInProject(
                            PROJECT_ID,
                            COLUMN_ID,
                            ColumnUpdateRequest.builder().name("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_IN_PROJECT);

            verify(columnRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: không có quyền quản lý, trả về lỗi ACCESS_DENIED")
        void updateColumnInProject_AccessDenied_ShouldThrow() {
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> columnService.updateColumnInProject(
                            PROJECT_ID,
                            COLUMN_ID,
                            ColumnUpdateRequest.builder().name("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(columnRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Column In Project: test hàm deleteColumnInProject")
    class DeleteColumnInProjectTest {

        @Test
        @DisplayName("Success: xóa column thành công")
        void deleteColumnInProject_ValidRequest_ShouldDeleteColumn() {
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);

            assertThatCode(() -> columnService.deleteColumnInProject(PROJECT_ID, COLUMN_ID))
                    .doesNotThrowAnyException();

            verify(columnRepository, times(1)).delete(column);
        }

        @Test
        @DisplayName("Fail: column không thuộc project này, trả về lỗi COLUMN_NOT_IN_PROJECT")
        void deleteColumnInProject_ColumnNotInProject_ShouldThrow() {
            column.setProjectId("other-project-uuid");

            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));

            assertThatThrownBy(() -> columnService.deleteColumnInProject(PROJECT_ID, COLUMN_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_IN_PROJECT);

            verify(columnRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Fail: không có quyền quản lý, trả về lỗi ACCESS_DENIED")
        void deleteColumnInProject_AccessDenied_ShouldThrow() {
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> columnService.deleteColumnInProject(PROJECT_ID, COLUMN_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(columnRepository, never()).delete(any());
        }
    }
}
