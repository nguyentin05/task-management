package com.ntt.task_service.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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
import com.ntt.task_service.dto.request.ColumnCreationRequest;
import com.ntt.task_service.dto.request.ColumnUpdateRequest;
import com.ntt.task_service.dto.response.ColumnResponse;
import com.ntt.task_service.dto.response.PageResponse;
import com.ntt.task_service.service.ColumnService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ColumnController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ColumnControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ColumnService columnService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    private static final String PROJECT_ID = "project-uuid-1234";
    private static final String COLUMN_ID = "column-uuid-1234";

    @Nested
    @DisplayName("Create Column In Project: test hàm createColumnInProject")
    class CreateColumnInProjectTest {

        @Test
        @DisplayName("Create Column - Success: tạo column thành công, trả về ColumnResponse")
        @WithMockUser(username = "user-uuid-1234")
        void createColumnInProject_ValidRequest_ShouldReturnColumnResponse() throws Exception {
            ColumnCreationRequest request =
                    ColumnCreationRequest.builder().name("To Do").position(1.0).build();

            ColumnResponse mockResponse = ColumnResponse.builder()
                    .id(COLUMN_ID)
                    .projectId(PROJECT_ID)
                    .name("To Do")
                    .position(1.0)
                    .build();

            when(columnService.createColumnInProject(eq(PROJECT_ID), any(ColumnCreationRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/projects/{projectId}/columns", PROJECT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(COLUMN_ID))
                    .andExpect(jsonPath("$.result.name").value("To Do"))
                    .andExpect(jsonPath("$.result.position").value(1.0));

            verify(columnService, times(1)).createColumnInProject(eq(PROJECT_ID), any(ColumnCreationRequest.class));
        }

        @Test
        @DisplayName("Create Column - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void createColumnInProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            ColumnCreationRequest request =
                    ColumnCreationRequest.builder().name("To Do").position(1.0).build();

            mockMvc.perform(post("/projects/{projectId}/columns", PROJECT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(columnService, never()).createColumnInProject(any(), any());
        }

        static Stream<Arguments> provideInvalidCreationRequests() {
            String tooLongName = "test".repeat(64);

            return Stream.of(
                    Arguments.of(ColumnCreationRequest.builder().position(1.0).build(), 3001),
                    Arguments.of(ColumnCreationRequest.builder().name("To Do").build(), 3001),
                    Arguments.of(
                            ColumnCreationRequest.builder()
                                    .name(tooLongName)
                                    .position(1.0)
                                    .build(),
                            3004),
                    Arguments.of(
                            ColumnCreationRequest.builder()
                                    .name("To Do")
                                    .position(-1.0)
                                    .build(),
                            3013));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCreationRequests")
        @WithMockUser
        @DisplayName("Create Column - Fail: bắt lỗi validation, trả về bad request")
        void createColumnInProject_InvalidRequest_ShouldReturnBadRequest(
                ColumnCreationRequest invalidRequest, int errorCode) throws Exception {
            mockMvc.perform(post("/projects/{projectId}/columns", PROJECT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(columnService, never()).createColumnInProject(any(), any());
        }
    }

    @Nested
    @DisplayName("Get All Column In Project: test hàm getAllColumnInProject")
    class GetAllColumnInProjectTest {

        @Test
        @DisplayName(
                "Get All Column - Success: lấy danh sách column phân trang thành công, trả về PageResponse<ColumnResponse>")
        @WithMockUser(username = "user-uuid-1234")
        void getAllColumnInProject_Authenticated_ShouldReturnPageResponse() throws Exception {
            ColumnResponse col1 =
                    ColumnResponse.builder().id("col-uuid-1").name("To Do").build();
            ColumnResponse col2 = ColumnResponse.builder()
                    .id("col-uuid-2")
                    .name("In Progress")
                    .build();
            ColumnResponse col3 =
                    ColumnResponse.builder().id("col-uuid-3").name("Done").build();

            PageResponse<ColumnResponse> mockPageResponse = PageResponse.<ColumnResponse>builder()
                    .currentPage(1)
                    .pageSize(10)
                    .totalElements(3)
                    .totalPages(1)
                    .data(List.of(col1, col2, col3))
                    .build();

            int page = 1;
            int size = 10;

            when(columnService.getAllColumnInProject(PROJECT_ID, page, size)).thenReturn(mockPageResponse);

            mockMvc.perform(get("/projects/{projectId}/columns", PROJECT_ID)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.currentPage").value(1))
                    .andExpect(jsonPath("$.result.pageSize").value(10))
                    .andExpect(jsonPath("$.result.totalElements").value(3))
                    .andExpect(jsonPath("$.result.data").isArray())
                    .andExpect(jsonPath("$.result.data.size()").value(3))
                    .andExpect(jsonPath("$.result.data[0].name").value("To Do"));

            verify(columnService, times(1)).getAllColumnInProject(PROJECT_ID, page, size);
        }

        @Test
        @DisplayName("Get All Column - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getAllColumnInProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/projects/{projectId}/columns", PROJECT_ID).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(columnService, never()).getAllColumnInProject(anyString(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Update Column In Project: test hàm updateColumnInProject")
    class UpdateColumnInProjectTest {

        @Test
        @DisplayName("Update Column - Success: cập nhật column thành công, trả về ColumnResponse")
        @WithMockUser(username = "user-uuid-1234")
        void updateColumnInProject_ValidRequest_ShouldReturnColumnResponse() throws Exception {
            ColumnUpdateRequest request = ColumnUpdateRequest.builder()
                    .name("In Progress")
                    .position(2.0)
                    .build();

            ColumnResponse mockResponse = ColumnResponse.builder()
                    .id(COLUMN_ID)
                    .projectId(PROJECT_ID)
                    .name("In Progress")
                    .position(2.0)
                    .build();

            when(columnService.updateColumnInProject(eq(PROJECT_ID), eq(COLUMN_ID), any(ColumnUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(patch("/projects/{projectId}/columns/{columnId}", PROJECT_ID, COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(COLUMN_ID))
                    .andExpect(jsonPath("$.result.name").value("In Progress"))
                    .andExpect(jsonPath("$.result.position").value(2.0));

            verify(columnService, times(1))
                    .updateColumnInProject(eq(PROJECT_ID), eq(COLUMN_ID), any(ColumnUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Column - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateColumnInProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch("/projects/{projectId}/columns/{columnId}", PROJECT_ID, COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    ColumnUpdateRequest.builder().name("test").build())))
                    .andExpect(status().isUnauthorized());

            verify(columnService, never()).updateColumnInProject(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Update Column - Fail: name quá dài, trả về bad request")
        void updateColumnInProject_NameTooLong_ShouldReturnBadRequest() throws Exception {
            String tooLongName = "test".repeat(64);
            ColumnUpdateRequest invalidRequest =
                    ColumnUpdateRequest.builder().name(tooLongName).build();

            mockMvc.perform(patch("/projects/{projectId}/columns/{columnId}", PROJECT_ID, COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(3004));

            verify(columnService, never()).updateColumnInProject(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Update Column - Fail: position âm, trả về bad request")
        void updateColumnInProject_NegativePosition_ShouldReturnBadRequest() throws Exception {
            ColumnUpdateRequest invalidRequest =
                    ColumnUpdateRequest.builder().position(-1.0).build();

            mockMvc.perform(patch("/projects/{projectId}/columns/{columnId}", PROJECT_ID, COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(3013));

            verify(columnService, never()).updateColumnInProject(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Column In Project: test hàm deleteColumnInProject")
    class DeleteColumnInProjectTest {

        @Test
        @DisplayName("Delete Column - Success: xóa column thành công, trả về message")
        @WithMockUser(username = "user-uuid-1234")
        void deleteColumnInProject_Authenticated_ShouldReturnSuccessMessage() throws Exception {
            doNothing().when(columnService).deleteColumnInProject(PROJECT_ID, COLUMN_ID);

            mockMvc.perform(delete("/projects/{projectId}/columns/{columnId}", PROJECT_ID, COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Xóa cột thành công"));

            verify(columnService, times(1)).deleteColumnInProject(PROJECT_ID, COLUMN_ID);
        }

        @Test
        @DisplayName("Delete Column - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void deleteColumnInProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/projects/{projectId}/columns/{columnId}", PROJECT_ID, COLUMN_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(columnService, never()).deleteColumnInProject(any(), any());
        }
    }
}
