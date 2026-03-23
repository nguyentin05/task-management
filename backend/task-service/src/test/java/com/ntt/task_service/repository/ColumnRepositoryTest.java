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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    @DisplayName("findIdsByProjectId: test hàm lấy danh sách ID phân trang")
    class FindIdsByProjectIdTest {

        @Test
        @DisplayName("Success: trả về Page chứa Column IDs, đúng project và đúng thứ tự position")
        void findIdsByProjectId_ShouldReturnPageOfIds() {
            Pageable pageable = PageRequest.of(0, 2, Sort.by("position").ascending());

            Page<String> result = columnRepository.findIdsByProjectId(PROJECT_ID, pageable);

            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getContent()).hasSize(2);

            assertThat(result.getContent().get(0)).isEqualTo(col2.getId());
            assertThat(result.getContent().get(1)).isEqualTo(col3.getId());
        }

        @Test
        @DisplayName("Success: project không có cột nào, trả về Page rỗng")
        void findIdsByProjectId_NoColumns_ShouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<String> result = columnRepository.findIdsByProjectId("nonexistent-project", pageable);

            assertThat(result.isEmpty()).isTrue();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("findByIdsWithTasks: test hàm tìm column kèm tasks bằng danh sách ID")
    class FindByIdsWithTasksTest {

        @Test
        @DisplayName("Success: truyền list ID, trả về đúng các column đó kèm theo tasks (LEFT JOIN FETCH)")
        void findByIdsWithTasks_ShouldReturnColumnsWithTasks() {
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

            List<String> idsToFetch = List.of(col1.getId(), col2.getId());
            List<Column> result = columnRepository.findByIdsWithTasks(idsToFetch);

            assertThat(result).hasSize(2);

            Column fetchedCol2 = result.stream()
                    .filter(c -> c.getId().equals(col2.getId()))
                    .findFirst()
                    .orElseThrow();

            assertThat(fetchedCol2.getTasks()).hasSize(1);
            assertThat(fetchedCol2.getTasks().getFirst().getTitle()).isEqualTo("Task 1");
        }

        @Test
        @DisplayName("Success: list ID rỗng, trả về danh sách rỗng (tránh ném lỗi SQL)")
        void findByIdsWithTasks_EmptyIdsList_ShouldReturnEmptyList() {
            List<Column> result = columnRepository.findByIdsWithTasks(List.of());
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