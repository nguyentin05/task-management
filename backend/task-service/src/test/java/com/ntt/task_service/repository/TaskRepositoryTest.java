package com.ntt.task_service.repository;

import static org.assertj.core.api.Assertions.*;

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
                .isDoneColumn(false)
                .build());

        col2 = entityManager.persistAndFlush(Column.builder()
                .name("Done Column")
                .projectId(PROJECT_ID)
                .position(2.0)
                .isDoneColumn(true)
                .build());

        otherCol = entityManager.persistAndFlush(Column.builder()
                .name("Other Column")
                .projectId("other-project-id")
                .position(3.0)
                .isDoneColumn(false)
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(col1.getId())
                .title("Task 1")
                .position(1.0)
                .label(TaskLabel.RED)
                .build());
        entityManager.persistAndFlush(Task.builder()
                .columnId(col1.getId())
                .title("Task 2")
                .position(2.0)
                .label(TaskLabel.BLUE)
                .build());
        entityManager.persistAndFlush(Task.builder()
                .columnId(col1.getId())
                .title("Task 3")
                .position(3.0)
                .label(TaskLabel.RED)
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(col2.getId())
                .title("Task 4")
                .position(1.0)
                .label(TaskLabel.BLUE)
                .build());
        entityManager.persistAndFlush(Task.builder()
                .columnId(col2.getId())
                .title("Task 5")
                .position(2.0)
                .label(TaskLabel.RED)
                .build());

        entityManager.persistAndFlush(Task.builder()
                .columnId(otherCol.getId())
                .title("Other task")
                .position(1.0)
                .label(TaskLabel.RED)
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
    @DisplayName("countByColumnId: test đếm task trong done column")
    class CountByColumnIdTest {

        @Test
        @DisplayName("Success: đếm đúng số task trong done column")
        void countByColumnId_DoneColumn_ShouldReturnCorrectCount() {
            long count = taskRepository.countByColumnId(col2.getId());

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("Success: đếm task trong non-done column vẫn trả về đúng")
        void countByColumnId_NonDoneColumn_ShouldReturnCorrectCount() {
            long count = taskRepository.countByColumnId(col1.getId());

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Success: không tính task của column khác project")
        void countByColumnId_ShouldNotCountOtherProjectColumn() {
            long doneCount = taskRepository.countByColumnId(col2.getId());

            assertThat(doneCount).isEqualTo(2);
        }

        @Test
        @DisplayName("Success: column không có task nào, trả về 0")
        void countByColumnId_EmptyColumn_ShouldReturnZero() {
            Column emptyCol = entityManager.persistAndFlush(Column.builder()
                    .name("Empty Done Column")
                    .projectId(PROJECT_ID)
                    .position(3.0)
                    .isDoneColumn(true)
                    .build());

            long count = taskRepository.countByColumnId(emptyCol.getId());

            assertThat(count).isZero();
        }
    }
}
