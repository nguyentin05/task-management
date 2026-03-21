package com.ntt.task_service.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.task_service.configuration.CustomJwtDecoder;
import com.ntt.task_service.configuration.SecurityConfig;
import com.ntt.task_service.domain.TaskLabel;
import com.ntt.task_service.dto.request.TaskAssignRequest;
import com.ntt.task_service.dto.request.TaskCreationRequest;
import com.ntt.task_service.dto.request.TaskMoveRequest;
import com.ntt.task_service.dto.request.TaskUpdateRequest;
import com.ntt.task_service.dto.response.TaskResponse;
import com.ntt.task_service.service.TaskService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TaskController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TaskService taskService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    private static final String TASK_ID = "task-uuid-1234";
    private static final String COLUMN_ID = "column-uuid-1234";
    private static final String USER_ID = "user-uuid-1234";
    private static final Instant FUTURE = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant FUTURE_10 = Instant.now().plus(10, ChronoUnit.DAYS);

    @Nested
    @DisplayName("Create Task: test hàm createTask")
    class CreateTaskTest {

        @Test
        @DisplayName("Create Task - Success: tạo task thành công, trả về TaskResponse")
        @WithMockUser(username = USER_ID)
        void createTask_ValidRequest_ShouldReturnTaskResponse() throws Exception {
            TaskCreationRequest request = TaskCreationRequest.builder()
                    .title("Fix bug")
                    .startAt(FUTURE)
                    .dueAt(FUTURE_10)
                    .position(1.0)
                    .label(TaskLabel.RED)
                    .build();

            TaskResponse mockResponse = TaskResponse.builder()
                    .id(TASK_ID)
                    .columnId(COLUMN_ID)
                    .title("Fix bug")
                    .build();

            when(taskService.createTask(eq(COLUMN_ID), any(TaskCreationRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/columns/{columnId}/tasks", COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(TASK_ID))
                    .andExpect(jsonPath("$.result.title").value("Fix bug"));

            verify(taskService, times(1)).createTask(eq(COLUMN_ID), any(TaskCreationRequest.class));
        }

        @Test
        @DisplayName("Create Task - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void createTask_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            TaskCreationRequest request = TaskCreationRequest.builder()
                    .title("Fix bug")
                    .startAt(FUTURE)
                    .dueAt(FUTURE_10)
                    .position(1.0)
                    .label(TaskLabel.RED)
                    .build();

            mockMvc.perform(post("/columns/{columnId}/tasks", COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(taskService, never()).createTask(any(), any());
        }

        static Stream<Arguments> provideInvalidCreationRequests() {
            String tooLongTitle = "test".repeat(64);

            return Stream.of(
                    Arguments.of(
                            TaskCreationRequest.builder()
                                    .startAt(FUTURE)
                                    .dueAt(FUTURE_10)
                                    .position(1.0)
                                    .label(TaskLabel.RED)
                                    .build(),
                            3001),
                    Arguments.of(
                            TaskCreationRequest.builder()
                                    .title("Fix bug")
                                    .dueAt(FUTURE_10)
                                    .position(1.0)
                                    .label(TaskLabel.RED)
                                    .build(),
                            3001),
                    Arguments.of(
                            TaskCreationRequest.builder()
                                    .title("Fix bug")
                                    .startAt(FUTURE)
                                    .position(1.0)
                                    .label(TaskLabel.RED)
                                    .build(),
                            3001),
                    Arguments.of(
                            TaskCreationRequest.builder()
                                    .title("Fix bug")
                                    .startAt(FUTURE)
                                    .dueAt(FUTURE_10)
                                    .label(TaskLabel.RED)
                                    .build(),
                            3001),
                    Arguments.of(
                            TaskCreationRequest.builder()
                                    .title("Fix bug")
                                    .startAt(FUTURE)
                                    .dueAt(FUTURE_10)
                                    .position(1.0)
                                    .build(),
                            3001),
                    Arguments.of(
                            TaskCreationRequest.builder()
                                    .title(tooLongTitle)
                                    .startAt(FUTURE)
                                    .dueAt(FUTURE_10)
                                    .position(1.0)
                                    .label(TaskLabel.RED)
                                    .build(),
                            3004),
                    Arguments.of(
                            TaskCreationRequest.builder()
                                    .title("Fix bug")
                                    .startAt(FUTURE)
                                    .dueAt(FUTURE_10)
                                    .position(-1.0)
                                    .label(TaskLabel.RED)
                                    .build(),
                            3013));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCreationRequests")
        @WithMockUser
        @DisplayName("Create Task - Fail: bắt lỗi validation, trả về bad request")
        void createTask_InvalidRequest_ShouldReturnBadRequest(TaskCreationRequest invalidRequest, int errorCode)
                throws Exception {
            mockMvc.perform(post("/columns/{columnId}/tasks", COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(taskService, never()).createTask(any(), any());
        }
    }

    @Nested
    @DisplayName("Get Task: test hàm getTask")
    class GetTaskTest {

        @Test
        @DisplayName("Get Task - Success: lấy chi tiết task thành công, trả về TaskResponse")
        @WithMockUser(username = USER_ID)
        void getTask_Authenticated_ShouldReturnTaskResponse() throws Exception {
            TaskResponse mockResponse = TaskResponse.builder()
                    .id(TASK_ID)
                    .columnId(COLUMN_ID)
                    .title("Fix bug")
                    .build();

            when(taskService.getTask(COLUMN_ID, TASK_ID)).thenReturn(mockResponse);

            mockMvc.perform(get("/columns/{columnId}/tasks/{taskId}", COLUMN_ID, TASK_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(TASK_ID))
                    .andExpect(jsonPath("$.result.columnId").value(COLUMN_ID));

            verify(taskService, times(1)).getTask(COLUMN_ID, TASK_ID);
        }

        @Test
        @DisplayName("Get Task - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getTask_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/columns/{columnId}/tasks/{taskId}", COLUMN_ID, TASK_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(taskService, never()).getTask(any(), any());
        }
    }

    @Nested
    @DisplayName("Update Task: test hàm updateTask")
    class UpdateTaskTest {

        @Test
        @DisplayName("Update Task - Success: cập nhật task thành công, trả về TaskResponse")
        @WithMockUser(username = USER_ID)
        void updateTask_ValidRequest_ShouldReturnTaskResponse() throws Exception {
            TaskUpdateRequest request = TaskUpdateRequest.builder()
                    .title("Updated title")
                    .label(TaskLabel.BLUE)
                    .build();

            TaskResponse mockResponse =
                    TaskResponse.builder().id(TASK_ID).title("Updated title").build();

            when(taskService.updateTask(eq(TASK_ID), any(TaskUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(patch("/tasks/{taskId}", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(TASK_ID))
                    .andExpect(jsonPath("$.result.title").value("Updated title"));

            verify(taskService, times(1)).updateTask(eq(TASK_ID), any(TaskUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Task - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateTask_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch("/tasks/{taskId}", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    TaskUpdateRequest.builder().title("test").build())))
                    .andExpect(status().isUnauthorized());

            verify(taskService, never()).updateTask(any(), any());
        }

        static Stream<Arguments> provideInvalidUpdateRequests() {
            String tooLongTitle = "test".repeat(64);

            return Stream.of(
                    Arguments.of(TaskUpdateRequest.builder().title(tooLongTitle).build(), 3004),
                    Arguments.of(
                            TaskUpdateRequest.builder()
                                    .startAt(Instant.now().minus(1, ChronoUnit.DAYS))
                                    .build(),
                            3010));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidUpdateRequests")
        @WithMockUser
        @DisplayName("Update Task - Fail: bắt lỗi validation, trả về bad request")
        void updateTask_InvalidRequest_ShouldReturnBadRequest(TaskUpdateRequest invalidRequest, int errorCode)
                throws Exception {
            mockMvc.perform(patch("/tasks/{taskId}", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(taskService, never()).updateTask(any(), any());
        }
    }

    @Nested
    @DisplayName("Move Task: test hàm moveTask")
    class MoveTaskTest {

        @Test
        @DisplayName("Move Task - Success: chuyển task thành công, trả về TaskResponse")
        @WithMockUser(username = USER_ID)
        void moveTask_ValidRequest_ShouldReturnTaskResponse() throws Exception {
            TaskMoveRequest request = TaskMoveRequest.builder()
                    .columnId("column-uuid-5678")
                    .position(2.0)
                    .build();

            TaskResponse mockResponse = TaskResponse.builder()
                    .id(TASK_ID)
                    .columnId("column-uuid-5678")
                    .build();

            when(taskService.moveTask(eq(TASK_ID), any(TaskMoveRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(put("/tasks/{taskId}/move", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(TASK_ID))
                    .andExpect(jsonPath("$.result.columnId").value("column-uuid-5678"));

            verify(taskService, times(1)).moveTask(eq(TASK_ID), any(TaskMoveRequest.class));
        }

        @Test
        @DisplayName("Move Task - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void moveTask_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put("/tasks/{taskId}/move", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(TaskMoveRequest.builder()
                                    .columnId("col-uuid")
                                    .position(1.0)
                                    .build())))
                    .andExpect(status().isUnauthorized());

            verify(taskService, never()).moveTask(any(), any());
        }

        static Stream<Arguments> provideInvalidMoveRequests() {
            return Stream.of(
                    Arguments.of(TaskMoveRequest.builder().position(1.0).build(), 3001),
                    Arguments.of(TaskMoveRequest.builder().columnId("col-uuid").build(), 3001),
                    Arguments.of(
                            TaskMoveRequest.builder()
                                    .columnId("col-uuid")
                                    .position(-1.0)
                                    .build(),
                            3013));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidMoveRequests")
        @WithMockUser
        @DisplayName("Move Task - Fail: bắt lỗi validation, trả về bad request")
        void moveTask_InvalidRequest_ShouldReturnBadRequest(TaskMoveRequest invalidRequest, int errorCode)
                throws Exception {
            mockMvc.perform(put("/tasks/{taskId}/move", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(taskService, never()).moveTask(any(), any());
        }
    }

    @Nested
    @DisplayName("Assign Member: test hàm assignMember")
    class AssignMemberTest {

        @Test
        @DisplayName("Assign Member - Success: gán thành viên vào task thành công, trả về TaskResponse")
        @WithMockUser(username = USER_ID)
        void assignMember_ValidRequest_ShouldReturnTaskResponse() throws Exception {
            TaskAssignRequest request =
                    TaskAssignRequest.builder().userId(USER_ID).build();

            TaskResponse mockResponse =
                    TaskResponse.builder().id(TASK_ID).assigneeId(USER_ID).build();

            when(taskService.assignMember(eq(TASK_ID), any(TaskAssignRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/tasks/{taskId}/assignees", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(TASK_ID))
                    .andExpect(jsonPath("$.result.assigneeId").value(USER_ID));

            verify(taskService, times(1)).assignMember(eq(TASK_ID), any(TaskAssignRequest.class));
        }

        @Test
        @DisplayName("Assign Member - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void assignMember_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post("/tasks/{taskId}/assignees", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    TaskAssignRequest.builder().userId(USER_ID).build())))
                    .andExpect(status().isUnauthorized());

            verify(taskService, never()).assignMember(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Assign Member - Fail: thiếu userId, trả về bad request")
        void assignMember_MissingUserId_ShouldReturnBadRequest() throws Exception {
            TaskAssignRequest invalidRequest = TaskAssignRequest.builder().build();

            mockMvc.perform(post("/tasks/{taskId}/assignees", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(3001));

            verify(taskService, never()).assignMember(any(), any());
        }
    }

    @Nested
    @DisplayName("Unassign Member: test hàm unassignMember")
    class UnassignMemberTest {

        @Test
        @DisplayName("Unassign Member - Success: xóa thành viên khỏi task thành công, trả về message")
        @WithMockUser(username = USER_ID)
        void unassignMember_Authenticated_ShouldReturnSuccessMessage() throws Exception {
            doNothing().when(taskService).unassignMember(TASK_ID, USER_ID);

            mockMvc.perform(delete("/tasks/{taskId}/assignees/{userId}", TASK_ID, USER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Đã xóa thành viên khỏi task"));

            verify(taskService, times(1)).unassignMember(TASK_ID, USER_ID);
        }

        @Test
        @DisplayName("Unassign Member - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void unassignMember_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/tasks/{taskId}/assignees/{userId}", TASK_ID, USER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(taskService, never()).unassignMember(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Task: test hàm deleteTask")
    class DeleteTaskTest {

        @Test
        @DisplayName("Delete Task - Success: xóa task thành công, trả về message")
        @WithMockUser(username = USER_ID)
        void deleteTask_Authenticated_ShouldReturnSuccessMessage() throws Exception {
            doNothing().when(taskService).deleteTask(TASK_ID);

            mockMvc.perform(delete("/tasks/{taskId}", TASK_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Xóa task thành công"));

            verify(taskService, times(1)).deleteTask(TASK_ID);
        }

        @Test
        @DisplayName("Delete Task - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void deleteTask_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/tasks/{taskId}", TASK_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(taskService, never()).deleteTask(any());
        }
    }
}
