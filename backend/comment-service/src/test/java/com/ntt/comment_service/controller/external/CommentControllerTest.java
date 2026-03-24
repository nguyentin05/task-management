package com.ntt.comment_service.controller.external;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.comment_service.configuration.CustomJwtDecoder;
import com.ntt.comment_service.configuration.SecurityConfig;
import com.ntt.comment_service.dto.request.CommentCreationRequest;
import com.ntt.comment_service.dto.request.CommentUpdateRequest;
import com.ntt.comment_service.dto.response.CommentResponse;
import com.ntt.comment_service.dto.response.PageResponse;
import com.ntt.comment_service.service.CommentService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CommentService commentService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    private static final String TASK_ID = "task-uuid-1234";
    private static final String COMMENT_ID = "comment-uuid-5678";

    @Nested
    @DisplayName("Get Comments Of Task: test hàm getCommentsOfTask")
    class GetCommentsOfTaskTest {

        @Test
        @DisplayName("Get Comments - Success: lấy danh sách comment phân trang thành công, trả về PageResponse<CommentResponse>")
        @WithMockUser(username = "user-uuid-1234")
        void getCommentsOfTask_Authenticated_ShouldReturnPageResponse() throws Exception {
            CommentResponse c1 = CommentResponse.builder().id("cmt-1").content("Hay quá").build();
            CommentResponse c2 = CommentResponse.builder().id("cmt-2").content("Đồng ý").build();

            int page = 1;
            int size = 10;

            PageResponse<CommentResponse> mockPageResponse = PageResponse.<CommentResponse>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalElements(2)
                    .totalPages(1)
                    .data(List.of(c1, c2))
                    .build();

            when(commentService.getCommentsByTask(TASK_ID, page, size)).thenReturn(mockPageResponse);

            mockMvc.perform(get("/tasks/{taskId}/comments", TASK_ID)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.currentPage").value(page))
                    .andExpect(jsonPath("$.result.pageSize").value(size))
                    .andExpect(jsonPath("$.result.totalElements").value(2))
                    .andExpect(jsonPath("$.result.data").isArray())
                    .andExpect(jsonPath("$.result.data.size()").value(2))
                    .andExpect(jsonPath("$.result.data[0].content").value("Hay quá"));

            verify(commentService, times(1)).getCommentsByTask(TASK_ID, page, size);
        }

        @Test
        @DisplayName("Get Comments - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getCommentsOfTask_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/tasks/{taskId}/comments", TASK_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(commentService, never()).getCommentsByTask(anyString(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Create Comment: test hàm createComment")
    class CreateCommentTest {

        @Test
        @DisplayName("Create Comment - Success: tạo comment thành công, trả về CommentResponse")
        @WithMockUser(username = "user-uuid-1234")
        void createComment_ValidRequest_ShouldReturnCommentResponse() throws Exception {
            CommentCreationRequest request = CommentCreationRequest.builder()
                    .content("Nhiệm vụ này cần đẩy nhanh")
                    .build();

            CommentResponse mockResponse = CommentResponse.builder()
                    .id(COMMENT_ID)
                    .content("Nhiệm vụ này cần đẩy nhanh")
                    .build();

            when(commentService.createComment(eq(TASK_ID), any(CommentCreationRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/tasks/{taskId}/comments", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(COMMENT_ID))
                    .andExpect(jsonPath("$.result.content").value("Nhiệm vụ này cần đẩy nhanh"));

            verify(commentService, times(1)).createComment(eq(TASK_ID), any(CommentCreationRequest.class));
        }

        @Test
        @DisplayName("Create Comment - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void createComment_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            CommentCreationRequest request = CommentCreationRequest.builder().content("Test").build();

            mockMvc.perform(post("/tasks/{taskId}/comments", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(commentService, never()).createComment(any(), any());
        }

        static Stream<Arguments> provideInvalidCreationRequests() {
            return Stream.of(
                    Arguments.of(CommentCreationRequest.builder().content("").build(), 3001),
                    Arguments.of(CommentCreationRequest.builder().content("   ").build(), 3001),
                    Arguments.of(CommentCreationRequest.builder().content(null).build(), 3001)
            );
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCreationRequests")
        @WithMockUser
        @DisplayName("Create Comment - Fail: bắt lỗi validation (content rỗng), trả về bad request")
        void createComment_InvalidRequest_ShouldReturnBadRequest(
                CommentCreationRequest invalidRequest, int errorCode) throws Exception {

            mockMvc.perform(post("/tasks/{taskId}/comments", TASK_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(commentService, never()).createComment(any(), any());
        }
    }

    @Nested
    @DisplayName("Update Comment: test hàm updateComment")
    class UpdateCommentTest {

        @Test
        @DisplayName("Update Comment - Success: cập nhật comment thành công, trả về CommentResponse")
        @WithMockUser(username = "user-uuid-1234")
        void updateComment_ValidRequest_ShouldReturnCommentResponse() throws Exception {
            CommentUpdateRequest request = CommentUpdateRequest.builder()
                    .content("Đã sửa lại nội dung")
                    .build();

            CommentResponse mockResponse = CommentResponse.builder()
                    .id(COMMENT_ID)
                    .content("Đã sửa lại nội dung")
                    .build();

            when(commentService.updateComment(eq(COMMENT_ID), any(CommentUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(put("/comments/{commentId}", COMMENT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(COMMENT_ID))
                    .andExpect(jsonPath("$.result.content").value("Đã sửa lại nội dung"));

            verify(commentService, times(1)).updateComment(eq(COMMENT_ID), any(CommentUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Comment - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateComment_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            CommentUpdateRequest request = CommentUpdateRequest.builder().content("Test").build();

            mockMvc.perform(put("/comments/{commentId}", COMMENT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(commentService, never()).updateComment(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Update Comment - Fail: bắt lỗi validation, trả về bad request")
        void updateComment_BlankContent_ShouldReturnBadRequest() throws Exception {
            CommentUpdateRequest invalidRequest = CommentUpdateRequest.builder().content("").build();

            mockMvc.perform(put("/comments/{commentId}", COMMENT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists());

            verify(commentService, never()).updateComment(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Comment: test hàm deleteComment")
    class DeleteCommentTest {

        @Test
        @DisplayName("Delete Comment - Success: xóa comment thành công, trả về message")
        @WithMockUser(username = "user-uuid-1234")
        void deleteComment_Authenticated_ShouldReturnSuccessMessage() throws Exception {
            doNothing().when(commentService).deleteComment(COMMENT_ID);

            mockMvc.perform(delete("/comments/{commentId}", COMMENT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Xóa comment thành công"));

            verify(commentService, times(1)).deleteComment(COMMENT_ID);
        }

        @Test
        @DisplayName("Delete Comment - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void deleteComment_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/comments/{commentId}", COMMENT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(commentService, never()).deleteComment(any());
        }
    }
}