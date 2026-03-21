package com.ntt.task_service.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.domain.ProjectRole;

@DataJpaTest
@ActiveProfiles("test")
class ProjectMemberRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    private static final String USER_ID_1 = "user-uuid-1111";
    private static final String USER_ID_2 = "user-uuid-2222";
    private static final String USER_ID_3 = "user-uuid-3333";

    private Project project;
    private Project otherProject;
    private ProjectMember member1;
    private ProjectMember member2;
    private ProjectMember member3;

    @BeforeEach
    void setUp() {
        project = entityManager.persistAndFlush(
                Project.builder().name("Test Project").createdBy(USER_ID_1).build());

        otherProject = entityManager.persistAndFlush(
                Project.builder().name("Other Project").createdBy(USER_ID_3).build());

        member1 = entityManager.persistAndFlush(ProjectMember.builder()
                .project(project)
                .userId(USER_ID_1)
                .role(ProjectRole.MANAGER)
                .build());

        member2 = entityManager.persistAndFlush(ProjectMember.builder()
                .project(project)
                .userId(USER_ID_2)
                .role(ProjectRole.MEMBER)
                .build());

        member3 = entityManager.persistAndFlush(ProjectMember.builder()
                .project(otherProject)
                .userId(USER_ID_3)
                .role(ProjectRole.MANAGER)
                .build());

        entityManager.clear();
    }

    @Nested
    @DisplayName("existsByProjectIdAndUserIdAndRole: test check tồn tại theo role")
    class ExistsByProjectIdAndUserIdAndRoleTest {

        @Test
        @DisplayName("Success: user có role đúng, trả về true")
        void existsByProjectIdAndUserIdAndRole_Exists_ShouldReturnTrue() {
            boolean result = projectMemberRepository.existsByProjectIdAndUserIdAndRole(
                    project.getId(), USER_ID_1, ProjectRole.MANAGER);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Success: user tồn tại nhưng sai role, trả về false")
        void existsByProjectIdAndUserIdAndRole_WrongRole_ShouldReturnFalse() {
            boolean result = projectMemberRepository.existsByProjectIdAndUserIdAndRole(
                    project.getId(), USER_ID_2, ProjectRole.MANAGER);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Success: user không tồn tại trong project, trả về false")
        void existsByProjectIdAndUserIdAndRole_UserNotInProject_ShouldReturnFalse() {
            boolean result = projectMemberRepository.existsByProjectIdAndUserIdAndRole(
                    project.getId(), USER_ID_3, ProjectRole.MEMBER);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByProjectIdAndUserId: test check tồn tại không phân biệt role")
    class ExistsByProjectIdAndUserIdTest {

        @Test
        @DisplayName("Success: user tồn tại trong project, trả về true")
        void existsByProjectIdAndUserId_Exists_ShouldReturnTrue() {
            boolean result = projectMemberRepository.existsByProjectIdAndUserId(project.getId(), USER_ID_2);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Success: user không tồn tại trong project, trả về false")
        void existsByProjectIdAndUserId_NotExists_ShouldReturnFalse() {
            boolean result = projectMemberRepository.existsByProjectIdAndUserId(project.getId(), USER_ID_3);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByProjectId: test lấy danh sách members")
    class FindByProjectIdTest {

        @Test
        @DisplayName("Success: trả về đúng members của project")
        void findByProjectId_ShouldReturnMembersOfProject() {
            List<ProjectMember> result = projectMemberRepository.findByProjectId(project.getId());

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ProjectMember::getUserId).containsExactlyInAnyOrder(USER_ID_1, USER_ID_2);
        }

        @Test
        @DisplayName("Success: không lẫn members của project khác")
        void findByProjectId_ShouldNotIncludeOtherProjects() {
            List<ProjectMember> result = projectMemberRepository.findByProjectId(project.getId());

            assertThat(result).noneMatch(m -> m.getUserId().equals(USER_ID_3));
        }

        @Test
        @DisplayName("Success: project không có member nào, trả về danh sách rỗng")
        void findByProjectId_NoMembers_ShouldReturnEmptyList() {
            Project emptyProject = entityManager.persistAndFlush(Project.builder()
                    .name("Empty Project")
                    .createdBy("some-user")
                    .build());
            entityManager.clear();

            List<ProjectMember> result = projectMemberRepository.findByProjectId(emptyProject.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUserIdsByProjectId: test lấy Set<userId>")
    class FindUserIdsByProjectIdTest {

        @Test
        @DisplayName("Success: trả về đúng Set userId của members trong project")
        void findUserIdsByProjectId_ShouldReturnUserIds() {
            Set<String> result = projectMemberRepository.findUserIdsByProjectId(project.getId());

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(USER_ID_1, USER_ID_2);
        }

        @Test
        @DisplayName("Success: không chứa userId của project khác")
        void findUserIdsByProjectId_ShouldNotIncludeOtherProjects() {
            Set<String> result = projectMemberRepository.findUserIdsByProjectId(project.getId());

            assertThat(result).doesNotContain(USER_ID_3);
        }

        @Test
        @DisplayName("Success: project không có member, trả về Set rỗng")
        void findUserIdsByProjectId_NoMembers_ShouldReturnEmptySet() {
            Project emptyProject = entityManager.persistAndFlush(
                    Project.builder().name("Empty").createdBy("some-user").build());
            entityManager.clear();

            Set<String> result = projectMemberRepository.findUserIdsByProjectId(emptyProject.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByProjectIdAndUserId: test tìm member cụ thể")
    class FindByProjectIdAndUserIdTest {

        @Test
        @DisplayName("Success: tìm thấy member, trả về Optional chứa ProjectMember")
        void findByProjectIdAndUserId_Exists_ShouldReturnMember() {
            Optional<ProjectMember> result =
                    projectMemberRepository.findByProjectIdAndUserId(project.getId(), USER_ID_1);

            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isEqualTo(USER_ID_1);
            assertThat(result.get().getRole()).isEqualTo(ProjectRole.MANAGER);
        }

        @Test
        @DisplayName("Success: user không trong project, trả về Optional.empty")
        void findByProjectIdAndUserId_NotExists_ShouldReturnEmpty() {
            Optional<ProjectMember> result =
                    projectMemberRepository.findByProjectIdAndUserId(project.getId(), USER_ID_3);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByProjectId: test đếm số members")
    class CountByProjectIdTest {

        @Test
        @DisplayName("Success: đếm đúng số member trong project")
        void countByProjectId_ShouldReturnCorrectCount() {
            long count = projectMemberRepository.countByProjectId(project.getId());

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("Success: project không có member, trả về 0")
        void countByProjectId_NoMembers_ShouldReturnZero() {
            Project emptyProject = entityManager.persistAndFlush(
                    Project.builder().name("Empty").createdBy("some-user").build());
            entityManager.clear();

            long count = projectMemberRepository.countByProjectId(emptyProject.getId());

            assertThat(count).isEqualTo(0);
        }
    }
}
