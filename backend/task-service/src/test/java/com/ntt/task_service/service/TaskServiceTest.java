package com.ntt.task_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.ntt.task_service.domain.Column;
import com.ntt.task_service.domain.Task;
import com.ntt.task_service.dto.request.TaskAssignRequest;
import com.ntt.task_service.dto.request.TaskCreationRequest;
import com.ntt.task_service.dto.request.TaskMoveRequest;
import com.ntt.task_service.dto.request.TaskUpdateRequest;
import com.ntt.task_service.dto.response.TaskResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.TaskMapper;
import com.ntt.task_service.repository.ColumnRepository;
import com.ntt.task_service.repository.ProjectMemberRepository;
import com.ntt.task_service.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskMapper taskMapper;

    @Mock
    TaskRepository taskRepository;

    @Mock
    ProjectAuthorizationService projectAuthorizationService;

    @Mock
    ColumnRepository columnRepository;

    @Mock
    ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    TaskService taskService;

    private static final String PROJECT_ID = "project-uuid-1234";
    private static final String COLUMN_ID = "column-uuid-1234";
    private static final String OTHER_COLUMN_ID = "column-uuid-9999";
    private static final String TASK_ID = "task-uuid-1234";
    private static final String USER_ID = "user-uuid-1234";
    private static final String OTHER_USER_ID = "user-uuid-9999";

    private Column column;
    private Task task;

    @BeforeEach
    void setUpGlobal() {
        column = Column.builder()
                .id(COLUMN_ID)
                .projectId(PROJECT_ID)
                .name("To Do")
                .build();

        task = Task.builder()
                .id(TASK_ID)
                .columnId(COLUMN_ID)
                .title("Test Task")
                .createdBy(USER_ID)
                .assigneeId(USER_ID)
                .build();
    }

    @Nested
    @DisplayName("Scenario - Fail: column không tồn tại (entry point là columnId)")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class ColumnNotFoundByColumnIdScenario {

        @BeforeEach
        void setupScenario() {
            when(columnRepository.findById(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Create Task: trả về lỗi COLUMN_NOT_FOUND")
        void createTaskTest() {
            assertThatThrownBy(() -> taskService.createTask(
                            COLUMN_ID,
                            TaskCreationRequest.builder().title("New Task").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);
        }

        @Test
        @DisplayName("Get Task: trả về lỗi COLUMN_NOT_FOUND")
        void getTaskTest() {
            assertThatThrownBy(() -> taskService.getTask(COLUMN_ID, TASK_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Scenario - Fail: task không tồn tại (entry point là taskId)")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class TaskNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(taskRepository.findById(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Update Task: trả về lỗi TASK_NOT_FOUND")
        void updateTaskTest() {
            assertThatThrownBy(() -> taskService.updateTask(
                            TASK_ID,
                            TaskUpdateRequest.builder().title("Updated").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TASK_NOT_FOUND);
        }

        @Test
        @DisplayName("Move Task: trả về lỗi TASK_NOT_FOUND")
        void moveTaskTest() {
            assertThatThrownBy(() -> taskService.moveTask(
                            TASK_ID,
                            TaskMoveRequest.builder()
                                    .columnId(OTHER_COLUMN_ID)
                                    .position(1.0)
                                    .build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TASK_NOT_FOUND);
        }

        @Test
        @DisplayName("Assign Member: trả về lỗi TASK_NOT_FOUND")
        void assignMemberTest() {
            assertThatThrownBy(() -> taskService.assignMember(
                            TASK_ID,
                            TaskAssignRequest.builder().userId(OTHER_USER_ID).build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TASK_NOT_FOUND);
        }

        @Test
        @DisplayName("Unassign Member: trả về lỗi TASK_NOT_FOUND")
        void unassignMemberTest() {
            assertThatThrownBy(() -> taskService.unassignMember(TASK_ID, USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TASK_NOT_FOUND);
        }

        @Test
        @DisplayName("Delete Task: trả về lỗi TASK_NOT_FOUND")
        void deleteTaskTest() {
            assertThatThrownBy(() -> taskService.deleteTask(TASK_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TASK_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Create Task: test hàm createTask")
    class CreateTaskTest {

        @Test
        @DisplayName("Success: tạo task thành công, trả về TaskResponse")
        void createTask_ValidRequest_ShouldReturnTaskResponse() {
            TaskCreationRequest request =
                    TaskCreationRequest.builder().title("New Task").build();

            TaskResponse mockResponse =
                    TaskResponse.builder().id(TASK_ID).title("New Task").build();

            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);
            when(taskMapper.toTask(request)).thenReturn(task);
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toTaskResponse(task)).thenReturn(mockResponse);

            TaskResponse result = taskService.createTask(COLUMN_ID, request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TASK_ID);
            assertThat(result.getTitle()).isEqualTo("New Task");

            verify(taskRepository, times(1)).save(any(Task.class));
            verify(projectAuthorizationService, times(1)).validateCanManage(PROJECT_ID);
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void createTask_AccessDenied_ShouldThrow() {
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> taskService.createTask(
                            COLUMN_ID,
                            TaskCreationRequest.builder().title("New Task").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Task: test hàm getTask")
    class GetTaskTest {

        @Test
        @DisplayName("Success: lấy task thành công, trả về TaskResponse")
        void getTask_ValidRequest_ShouldReturnTaskResponse() {
            TaskResponse mockResponse =
                    TaskResponse.builder().id(TASK_ID).title("Test Task").build();

            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(taskMapper.toTaskResponse(task)).thenReturn(mockResponse);

            TaskResponse result = taskService.getTask(COLUMN_ID, TASK_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TASK_ID);

            verify(taskRepository, times(1)).findById(TASK_ID);
            verify(projectAuthorizationService, times(1)).validateCanView(PROJECT_ID);
        }

        @Test
        @DisplayName("Fail: task không tồn tại, trả về lỗi TASK_NOT_FOUND")
        void getTask_TaskNotFound_ShouldThrow() {
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.getTask(COLUMN_ID, TASK_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TASK_NOT_FOUND);
        }

        @Test
        @DisplayName("Fail: task không thuộc column này, trả về lỗi TASK_NOT_IN_COLUMN")
        void getTask_TaskNotInColumn_ShouldThrow() {
            task.setColumnId(OTHER_COLUMN_ID);

            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

            assertThatThrownBy(() -> taskService.getTask(COLUMN_ID, TASK_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TASK_NOT_IN_COLUMN);
        }

        @Test
        @DisplayName("Fail: user không có quyền view, trả về lỗi ACCESS_DENIED")
        void getTask_AccessDenied_ShouldThrow() {
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanView(PROJECT_ID);

            assertThatThrownBy(() -> taskService.getTask(COLUMN_ID, TASK_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(taskRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("Update Task: test hàm updateTask")
    class UpdateTaskTest {

        private TaskUpdateRequest request;

        @BeforeEach
        void setup() {
            request = TaskUpdateRequest.builder().title("Updated Task").build();
        }

        @Test
        @DisplayName("Success: cập nhật task thành công, trả về TaskResponse")
        void updateTask_ValidRequest_ShouldReturnTaskResponse() {
            TaskResponse mockResponse =
                    TaskResponse.builder().id(TASK_ID).title("Updated Task").build();

            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toTaskResponse(task)).thenReturn(mockResponse);

            TaskResponse result = taskService.updateTask(TASK_ID, request);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Updated Task");

            verify(taskMapper, times(1)).updateTask(eq(task), eq(request));
            verify(taskRepository, times(1)).save(any(Task.class));
        }

        @Test
        @DisplayName("Fail: column của task không tồn tại, trả về lỗi COLUMN_NOT_FOUND")
        void updateTask_ColumnNotFound_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.updateTask(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền view, trả về lỗi ACCESS_DENIED")
        void updateTask_AccessDenied_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanView(PROJECT_ID);

            assertThatThrownBy(() -> taskService.updateTask(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Move Task: test hàm moveTask")
    class MoveTaskTest {

        private Column newColumn;
        private TaskMoveRequest request;

        @BeforeEach
        void setup() {
            newColumn = Column.builder()
                    .id(OTHER_COLUMN_ID)
                    .projectId(PROJECT_ID)
                    .name("In Progress")
                    .build();

            request = TaskMoveRequest.builder()
                    .columnId(OTHER_COLUMN_ID)
                    .position(2.0)
                    .build();
        }

        @Test
        @DisplayName("Success: di chuyển task thành công, trả về TaskResponse")
        void moveTask_ValidRequest_ShouldReturnTaskResponse() {
            TaskResponse mockResponse =
                    TaskResponse.builder().id(TASK_ID).columnId(OTHER_COLUMN_ID).build();

            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(columnRepository.findById(OTHER_COLUMN_ID)).thenReturn(Optional.of(newColumn));
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toTaskResponse(task)).thenReturn(mockResponse);

            TaskResponse result = taskService.moveTask(TASK_ID, request);

            assertThat(result).isNotNull();
            assertThat(result.getColumnId()).isEqualTo(OTHER_COLUMN_ID);

            verify(taskRepository, times(1)).save(any(Task.class));
        }

        @Test
        @DisplayName("Fail: column đích không tồn tại, trả về lỗi COLUMN_NOT_FOUND")
        void moveTask_NewColumnNotFound_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(columnRepository.findById(OTHER_COLUMN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.moveTask(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: column đích thuộc project khác, trả về lỗi COLUMN_NOT_IN_PROJECT")
        void moveTask_NewColumnInDifferentProject_ShouldThrow() {
            newColumn.setProjectId("other-project-uuid");

            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(columnRepository.findById(OTHER_COLUMN_ID)).thenReturn(Optional.of(newColumn));

            assertThatThrownBy(() -> taskService.moveTask(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_IN_PROJECT);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: column của task không tồn tại, trả về lỗi COLUMN_NOT_FOUND")
        void moveTask_CurrentColumnNotFound_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.moveTask(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền view, trả về lỗi ACCESS_DENIED")
        void moveTask_AccessDenied_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanView(PROJECT_ID);

            assertThatThrownBy(() -> taskService.moveTask(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Assign Member: test hàm assignMember")
    class AssignMemberTest {

        private TaskAssignRequest request;

        @BeforeEach
        void setup() {
            request = TaskAssignRequest.builder().userId(OTHER_USER_ID).build();
        }

        @Test
        @DisplayName("Success: gán assignee thành công, trả về TaskResponse")
        void assignMember_ValidRequest_ShouldReturnTaskResponse() {
            TaskResponse mockResponse =
                    TaskResponse.builder().id(TASK_ID).assigneeId(OTHER_USER_ID).build();

            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.existsByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(true);
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            when(taskMapper.toTaskResponse(task)).thenReturn(mockResponse);

            TaskResponse result = taskService.assignMember(TASK_ID, request);

            assertThat(result).isNotNull();
            assertThat(result.getAssigneeId()).isEqualTo(OTHER_USER_ID);

            verify(taskRepository, times(1)).save(any(Task.class));
        }

        @Test
        @DisplayName("Fail: user không có trong project, trả về lỗi USER_NOT_IN_PROJECT")
        void assignMember_UserNotInProject_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.existsByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(false);

            assertThatThrownBy(() -> taskService.assignMember(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_IN_PROJECT);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: column của task không tồn tại, trả về lỗi COLUMN_NOT_FOUND")
        void assignMember_ColumnNotFound_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.assignMember(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void assignMember_AccessDenied_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> taskService.assignMember(TASK_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Unassign Member: test hàm unassignMember")
    class UnassignMemberTest {

        @Test
        @DisplayName("Success: bỏ assignee thành công")
        void unassignMember_ValidRequest_ShouldClearAssignee() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);

            assertThatCode(() -> taskService.unassignMember(TASK_ID, USER_ID)).doesNotThrowAnyException();

            verify(taskRepository, times(1)).save(task);
        }

        @Test
        @DisplayName("Fail: userId không phải assignee hiện tại, trả về lỗi USER_NOT_ASSIGNED")
        void unassignMember_UserNotAssigned_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> taskService.unassignMember(TASK_ID, OTHER_USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_ASSIGNED);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: column của task không tồn tại, trả về lỗi COLUMN_NOT_FOUND")
        void unassignMember_ColumnNotFound_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.unassignMember(TASK_ID, USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void unassignMember_AccessDenied_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> taskService.unassignMember(TASK_ID, USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Task: test hàm deleteTask")
    class DeleteTaskTest {

        @Test
        @DisplayName("Success: xóa task thành công")
        void deleteTask_ValidRequest_ShouldDeleteTask() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);

            assertThatCode(() -> taskService.deleteTask(TASK_ID)).doesNotThrowAnyException();

            verify(taskRepository, times(1)).delete(task);
        }

        @Test
        @DisplayName("Fail: column của task không tồn tại, trả về lỗi COLUMN_NOT_FOUND")
        void deleteTask_ColumnNotFound_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.deleteTask(TASK_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);

            verify(taskRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void deleteTask_AccessDenied_ShouldThrow() {
            when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(columnRepository.findById(COLUMN_ID)).thenReturn(Optional.of(column));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> taskService.deleteTask(TASK_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(taskRepository, never()).delete(any());
        }
    }
}
