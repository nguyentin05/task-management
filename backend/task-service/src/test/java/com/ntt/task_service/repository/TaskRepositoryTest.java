package com.ntt.task_service.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.ntt.task_service.domain.Column;
import com.ntt.task_service.domain.Task;
import com.ntt.task_service.domain.TaskLabel;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    TaskRepository taskRepository;

    private static final String PROJECT_ID = "project-uuid-1234";

    private Column col1;
    private Column col2;
    private Column otherCol;

    @BeforeEach
    void setUp() {
        col1 = entityManager.persistAndFlush(Column.builder()
                .name("Column 1")
                .projectId(PROJECT_ID)
                .position(1.0)
                .build());

        col2 = entityManager.persistAndFlush(Column.builder()
                .name("Column 2")
                .projectId(PROJECT_ID)
                .position(2.0)
                .build());

        otherCol = entityManager.persistAndFlush(Column.builder()
                .name("Other Column")
                .projectId("other-project-id")
                .position(3.0)
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(col1.getId())
                .title("Task 1 - completed")
                .position(1.0)
                .label(TaskLabel.RED)
                .completedAt(Instant.now().minusSeconds(3600))
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(col1.getId())
                .title("Task 2 - completed")
                .position(2.0)
                .label(TaskLabel.BLUE)
                .completedAt(Instant.now().minusSeconds(1800))
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(col1.getId())
                .title("Task 3 - not completed")
                .position(3.0)
                .label(TaskLabel.RED)
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(col2.getId())
                .title("Task 4 - completed")
                .position(1.0)
                .label(TaskLabel.BLUE)
                .completedAt(Instant.now().minusSeconds(900))
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(col2.getId())
                .title("Task 5 - not completed")
                .position(2.0)
                .label(TaskLabel.RED)
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(otherCol.getId())
                .title("Other task - completed")
                .position(1.0)
                .label(TaskLabel.RED)
                .completedAt(Instant.now().minusSeconds(600))
                .build());
    }

    @Nested
    @DisplayName("countByColumnIdIn: test đếm tổng số task trong các column")
    class CountByColumnIdInTest {

        @Test
        @DisplayName("Success: đếm đúng tổng task trong nhiều column")
        void countByColumnIdIn_MultipleColumns_ShouldReturnCorrectCount() {
            long count = taskRepository.countByColumnIdIn(List.of(col1.getId(), col2.getId()));

            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("Success: đếm đúng task trong 1 column")
        void countByColumnIdIn_SingleColumn_ShouldReturnCorrectCount() {
            long count = taskRepository.countByColumnIdIn(List.of(col1.getId()));

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Success: không tính task của column không trong danh sách")
        void countByColumnIdIn_ShouldNotCountOtherColumns() {
            long count = taskRepository.countByColumnIdIn(List.of(col1.getId(), col2.getId()));

            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("Success: danh sách column rỗng, trả về 0")
        void countByColumnIdIn_EmptyList_ShouldReturnZero() {
            long count = taskRepository.countByColumnIdIn(List.of());

            assertThat(count).isZero();
        }
    }

    @Nested
    @DisplayName("countByColumnIdInAndCompletedAtIsNotNull: test đếm task đã hoàn thành")
    class CountByColumnIdInAndCompletedAtIsNotNullTest {

        @Test
        @DisplayName("Success: đếm đúng task đã hoàn thành trong nhiều column")
        void countByColumnIdInAndCompletedAtIsNotNull_MultipleColumns_ShouldReturnCorrectCount() {
            long count = taskRepository.countByColumnIdInAndCompletedAtIsNotNull(List.of(col1.getId(), col2.getId()));

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Success: đếm đúng task đã hoàn thành trong 1 column")
        void countByColumnIdInAndCompletedAtIsNotNull_SingleColumn_ShouldReturnCorrectCount() {
            long count = taskRepository.countByColumnIdInAndCompletedAtIsNotNull(List.of(col1.getId()));

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("Success: không tính task chưa hoàn thành (completedAt = null)")
        void countByColumnIdInAndCompletedAtIsNotNull_ShouldIgnoreNullCompletedAt() {
            long totalTasks = taskRepository.countByColumnIdIn(List.of(col1.getId()));
            long completedTasks = taskRepository.countByColumnIdInAndCompletedAtIsNotNull(List.of(col1.getId()));

            assertThat(totalTasks).isEqualTo(3);
            assertThat(completedTasks).isEqualTo(2);
            assertThat(completedTasks).isLessThan(totalTasks);
        }

        @Test
        @DisplayName("Success: không tính task completed của column không trong danh sách")
        void countByColumnIdInAndCompletedAtIsNotNull_ShouldNotCountOtherColumns() {
            long count = taskRepository.countByColumnIdInAndCompletedAtIsNotNull(List.of(col1.getId(), col2.getId()));

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Success: danh sách column rỗng, trả về 0")
        void countByColumnIdInAndCompletedAtIsNotNull_EmptyList_ShouldReturnZero() {
            long count = taskRepository.countByColumnIdInAndCompletedAtIsNotNull(List.of());

            assertThat(count).isZero();
        }
    }
}
