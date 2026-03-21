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
class ColumnRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ColumnRepository columnRepository;

    private static final String PROJECT_ID = "project-uuid-1234";
    private static final String OTHER_PROJECT_ID = "project-uuid-5678";

    private Column col1;
    private Column col2;
    private Column col3;

    @BeforeEach
    void setUp() {
        col1 = entityManager.persistAndFlush(Column.builder()
                .projectId(PROJECT_ID)
                .name("Done")
                .position(3.0)
                .build());

        col2 = entityManager.persistAndFlush(Column.builder()
                .projectId(PROJECT_ID)
                .name("To Do")
                .position(1.0)
                .build());

        col3 = entityManager.persistAndFlush(Column.builder()
                .projectId(PROJECT_ID)
                .name("In Progress")
                .position(2.0)
                .build());

        entityManager.persistAndFlush(Column.builder()
                .projectId(OTHER_PROJECT_ID)
                .name("Other Column")
                .position(1.0)
                .build());

        entityManager.clear();
    }

    @Nested
    @DisplayName("findByProjectIdWithTasks: test hàm tìm column kèm tasks")
    class FindByProjectIdWithTasksTest {

        @Test
        @DisplayName("Success: trả về đúng columns của project, sắp xếp theo position tăng dần")
        void findByProjectIdWithTasks_ShouldReturnOrderedByPosition() {
            List<Column> result = columnRepository.findByProjectIdWithTasks(PROJECT_ID);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getName()).isEqualTo("To Do");
            assertThat(result.get(1).getName()).isEqualTo("In Progress");
            assertThat(result.get(2).getName()).isEqualTo("Done");
        }

        @Test
        @DisplayName("Success: chỉ trả về columns của đúng project, không lẫn project khác")
        void findByProjectIdWithTasks_ShouldFilterByProjectId() {
            List<Column> result = columnRepository.findByProjectIdWithTasks(PROJECT_ID);

            assertThat(result).allMatch(c -> c.getProjectId().equals(PROJECT_ID));
        }

        @Test
        @DisplayName("Success: column có tasks, trả về tasks kèm theo (LEFT JOIN FETCH)")
        void findByProjectIdWithTasks_WithTasks_ShouldFetchTasksEagerly() {
            Column col2WithTasks = entityManager.find(Column.class, col2.getId());
            Task task = entityManager.persistAndFlush(Task.builder()
                    .columnId(col2WithTasks.getId())
                    .title("Task 1")
                    .position(1.0)
                    .label(TaskLabel.RED)
                    .build());
            col2WithTasks.getTasks().add(task);
            entityManager.persistAndFlush(col2WithTasks);
            entityManager.clear();

            List<Column> result = columnRepository.findByProjectIdWithTasks(PROJECT_ID);

            Column columnWithTask = result.stream()
                    .filter(c -> c.getId().equals(col2.getId()))
                    .findFirst()
                    .orElseThrow();

            assertThat(columnWithTask.getTasks()).hasSize(1);
            assertThat(columnWithTask.getTasks().get(0).getTitle()).isEqualTo("Task 1");
        }

        @Test
        @DisplayName("Success: column không có task nào, tasks trả về rỗng")
        void findByProjectIdWithTasks_ColumnWithNoTasks_ShouldReturnEmptyTasks() {
            List<Column> result = columnRepository.findByProjectIdWithTasks(PROJECT_ID);

            assertThat(result).allMatch(c -> c.getTasks().isEmpty());
        }

        @Test
        @DisplayName("Success: không có column nào trong project, trả về danh sách rỗng")
        void findByProjectIdWithTasks_NoColumns_ShouldReturnEmptyList() {
            List<Column> result = columnRepository.findByProjectIdWithTasks("nonexistent-project");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findColumnIdsByProjectId: test hàm lấy danh sách columnId")
    class FindColumnIdsByProjectIdTest {

        @Test
        @DisplayName("Success: trả về đúng danh sách id của columns trong project")
        void findColumnIdsByProjectId_ShouldReturnColumnIds() {
            List<String> result = columnRepository.findColumnIdsByProjectId(PROJECT_ID);

            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyInAnyOrder(col1.getId(), col2.getId(), col3.getId());
        }

        @Test
        @DisplayName("Success: chỉ trả về id của columns thuộc đúng project")
        void findColumnIdsByProjectId_ShouldFilterByProjectId() {
            List<String> result = columnRepository.findColumnIdsByProjectId(PROJECT_ID);
            List<String> otherResult = columnRepository.findColumnIdsByProjectId(OTHER_PROJECT_ID);

            assertThat(result).doesNotContainAnyElementsOf(otherResult);
        }

        @Test
        @DisplayName("Success: không có column nào, trả về danh sách rỗng")
        void findColumnIdsByProjectId_NoColumns_ShouldReturnEmptyList() {
            List<String> result = columnRepository.findColumnIdsByProjectId("nonexistent-project");

            assertThat(result).isEmpty();
        }
    }
}
