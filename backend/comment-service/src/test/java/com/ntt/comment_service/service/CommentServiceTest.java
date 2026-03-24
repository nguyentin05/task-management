package com.ntt.comment_service.service;

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

import com.ntt.comment_service.domain.Comment;
import com.ntt.comment_service.dto.request.CommentCreationRequest;
import com.ntt.comment_service.dto.request.CommentUpdateRequest;
import com.ntt.comment_service.dto.response.CommentResponse;
import com.ntt.comment_service.dto.response.PageResponse;
import com.ntt.comment_service.exception.AppException;
import com.ntt.comment_service.exception.ErrorCode;
import com.ntt.comment_service.mapper.CommentMapper;
import com.ntt.comment_service.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    CommentMapper commentMapper;

    @Mock
    CommentAuthorizationService commentAuthorizationService;

    @InjectMocks
    CommentService commentService;

    private static final String USER_ID = "user-uuid-1234";
    private static final String OTHER_USER_ID = "other-uuid-5678";
    private static final String TASK_ID = "task-uuid-1234";
    private static final String COMMENT_ID = "comment-uuid-1234";

    private Comment comment;

    @BeforeEach
    void setUpGlobal() {
        comment = Comment.builder()
                .id(COMMENT_ID)
                .taskId(TASK_ID)
                .userId(USER_ID)
                .content("Nội dung gốc")
                .build();
    }

    @Nested
    @DisplayName("Scenario - Fail: comment không tồn tại")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class CommentNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(commentAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);
            when(commentRepository.findById(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Update Comment: trả về lỗi COMMENT_NOT_FOUND")
        void updateCommentTest() {
            assertThatThrownBy(() -> commentService.updateComment(
                            COMMENT_ID,
                            CommentUpdateRequest.builder().content("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);

            verify(commentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Delete Comment: trả về lỗi COMMENT_NOT_FOUND")
        void deleteCommentTest() {
            when(commentAuthorizationService.isAdmin()).thenReturn(false);

            assertThatThrownBy(() -> commentService.deleteComment(COMMENT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);

            verify(commentRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Get Comments By Task: test hàm getCommentsByTask")
    class GetCommentsByTaskTest {

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        @Test
        @DisplayName("Success: lấy bình luận phân trang thành công")
        void getCommentsByTask_ShouldReturnPageResponse() {
            Comment comment2 =
                    Comment.builder().id("cmt-2").content("Bình luận 2").build();
            CommentResponse res1 = CommentResponse.builder().id(COMMENT_ID).build();
            CommentResponse res2 = CommentResponse.builder().id("cmt-2").build();

            Page<Comment> pageResult = new PageImpl<>(List.of(comment, comment2), pageable, 2);

            when(commentRepository.findByTaskId(eq(TASK_ID), any(Pageable.class)))
                    .thenReturn(pageResult);
            when(commentMapper.toCommentResponse(comment)).thenReturn(res1);
            when(commentMapper.toCommentResponse(comment2)).thenReturn(res2);

            PageResponse<CommentResponse> result = commentService.getCommentsByTask(TASK_ID, page, size);

            assertThat(result.getCurrentPage()).isEqualTo(page);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getData()).hasSize(2);

            verify(commentRepository, times(1)).findByTaskId(eq(TASK_ID), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Create Comment: test hàm createComment")
    class CreateCommentTest {

        @Test
        @DisplayName("Success: tạo comment thành công, set đúng ID và trả về CommentResponse")
        void createComment_ValidRequest_ShouldReturnCommentResponse() {
            CommentCreationRequest request =
                    CommentCreationRequest.builder().content("Mới tạo").build();
            CommentResponse mockResponse =
                    CommentResponse.builder().id(COMMENT_ID).content("Mới tạo").build();

            when(commentAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);
            when(commentMapper.toComment(request)).thenReturn(comment);
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toCommentResponse(comment)).thenReturn(mockResponse);

            CommentResponse response = commentService.createComment(TASK_ID, request);

            assertThat(response).isNotNull();
            assertThat(comment.getTaskId()).isEqualTo(TASK_ID);
            assertThat(comment.getUserId()).isEqualTo(USER_ID);

            verify(commentRepository, times(1)).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("Update Comment: test hàm updateComment")
    class UpdateCommentTest {

        @Test
        @DisplayName("Success: user là chủ sở hữu, cập nhật thành công")
        void updateComment_ValidRequest_ShouldUpdateAndReturnResponse() {
            CommentUpdateRequest request =
                    CommentUpdateRequest.builder().content("Đã sửa").build();
            CommentResponse mockResponse =
                    CommentResponse.builder().id(COMMENT_ID).content("Đã sửa").build();

            when(commentAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);
            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
            doNothing().when(commentMapper).updateComment(comment, request);
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toCommentResponse(comment)).thenReturn(mockResponse);

            CommentResponse response = commentService.updateComment(COMMENT_ID, request);

            assertThat(response).isNotNull();
            assertThat(comment.isEdited()).isTrue();

            verify(commentRepository, times(1)).save(comment);
        }

        @Test
        @DisplayName("Fail: user không phải chủ sở hữu, trả về lỗi ACCESS_DENIED")
        void updateComment_NotOwner_ShouldThrowAccessDenied() {
            when(commentAuthorizationService.getCurrentUserId()).thenReturn(OTHER_USER_ID);
            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

            assertThatThrownBy(() -> commentService.updateComment(
                            COMMENT_ID,
                            CommentUpdateRequest.builder().content("hack").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(commentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Comment: test hàm deleteComment")
    class DeleteCommentTest {

        @Test
        @DisplayName("Success: user là chủ sở hữu, xóa thành công")
        void deleteComment_IsOwner_ShouldDelete() {
            when(commentAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);
            when(commentAuthorizationService.isAdmin()).thenReturn(false);
            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

            assertThatCode(() -> commentService.deleteComment(COMMENT_ID)).doesNotThrowAnyException();

            verify(commentRepository, times(1)).delete(comment);
        }

        @Test
        @DisplayName("Success: user là ADMIN, xóa thành công")
        void deleteComment_IsAdmin_ShouldDelete() {
            when(commentAuthorizationService.getCurrentUserId()).thenReturn(OTHER_USER_ID);
            when(commentAuthorizationService.isAdmin()).thenReturn(true);
            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

            assertThatCode(() -> commentService.deleteComment(COMMENT_ID)).doesNotThrowAnyException();

            verify(commentRepository, times(1)).delete(comment);
        }

        @Test
        @DisplayName("Fail: user không phải chủ và không phải ADMIN, trả về lỗi ACCESS_DENIED")
        void deleteComment_NotOwnerAndNotAdmin_ShouldThrowAccessDenied() {
            when(commentAuthorizationService.getCurrentUserId()).thenReturn(OTHER_USER_ID);
            when(commentAuthorizationService.isAdmin()).thenReturn(false);
            when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

            assertThatThrownBy(() -> commentService.deleteComment(COMMENT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(commentRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Delete Comment By Task: test hàm deleteCommentByTask")
    class DeleteCommentByTaskTest {

        @Test
        @DisplayName("Success: gọi hàm xóa comment theo taskId thành công")
        void deleteCommentByTask_ShouldCallRepository() {
            doNothing().when(commentRepository).deleteByTaskId(TASK_ID);

            assertThatCode(() -> commentService.deleteCommentByTask(TASK_ID)).doesNotThrowAnyException();

            verify(commentRepository, times(1)).deleteByTaskId(TASK_ID);
        }
    }
}
