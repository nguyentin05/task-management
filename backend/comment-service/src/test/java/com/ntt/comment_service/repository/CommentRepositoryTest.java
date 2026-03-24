package com.ntt.comment_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ntt.comment_service.domain.Comment;

@DataMongoTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private static final String TASK_1 = "task-uuid-1";
    private static final String TASK_2 = "task-uuid-2";

    @BeforeEach
    void setUp() {
        Comment c1 = Comment.builder().taskId(TASK_1).content("Comment 1").build();
        Comment c2 = Comment.builder().taskId(TASK_1).content("Comment 2").build();
        Comment c3 = Comment.builder().taskId(TASK_1).content("Comment 3").build();
        Comment c4 = Comment.builder().taskId(TASK_2).content("Comment 4").build();

        commentRepository.saveAll(List.of(c1, c2, c3, c4));
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
    }

    @Nested
    @DisplayName("findByTaskId: Test hàm lấy comment theo taskId")
    class FindByTaskIdTest {

        @Test
        @DisplayName("Success: Lấy danh sách comment phân trang chuẩn xác")
        void findByTaskId_ShouldReturnPagedComments() {
            Pageable pageable = PageRequest.of(0, 2);

            Page<Comment> result = commentRepository.findByTaskId(TASK_1, pageable);

            assertThat(result.getTotalElements()).isEqualTo(3);

            assertThat(result.getTotalPages()).isEqualTo(2);

            assertThat(result.getContent()).hasSize(2);

            assertThat(result.getContent().getFirst().getTaskId()).isEqualTo(TASK_1);
        }

        @Test
        @DisplayName("Success: Không có comment nào cho task này, trả về list rỗng")
        void findByTaskId_NoComments_ShouldReturnEmpty() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Comment> result = commentRepository.findByTaskId("task-không-tồn-tại", pageable);

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteByTaskId: Test hàm xóa tất cả comment của 1 task")
    class DeleteByTaskIdTest {

        @Test
        @DisplayName("Success: Xóa sạch comment của TASK_1, giữ nguyên TASK_2")
        void deleteByTaskId_ShouldDeleteOnlyTargetTaskComments() {
            commentRepository.deleteByTaskId(TASK_1);

            Page<Comment> remainingTask1Comments = commentRepository.findByTaskId(TASK_1, Pageable.unpaged());
            Page<Comment> remainingTask2Comments = commentRepository.findByTaskId(TASK_2, Pageable.unpaged());

            assertThat(remainingTask1Comments.getContent()).isEmpty();

            assertThat(remainingTask2Comments.getContent()).hasSize(1);
        }
    }
}