package com.ntt.task_service.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.ntt.task_service.domain.Workspace;

@DataJpaTest
@ActiveProfiles("test")
class WorkspaceRepositoryTest {

    @Autowired
    WorkspaceRepository workspaceRepository;

    private static final String USER_ID = "user-uuid-1234";
    private static final String OTHER_USER_ID = "user-uuid-9999";

    @BeforeEach
    void setUp() {
        workspaceRepository.saveAll(
                List.of(buildWorkspace(USER_ID, "My Workspace"), buildWorkspace(OTHER_USER_ID, "Other Workspace")));
    }

    @Nested
    @DisplayName("findByUserId: test hàm tìm workspace theo userId")
    class FindByUserIdTest {

        @Test
        @DisplayName("Success: tìm thấy workspace của user, trả về Optional chứa workspace")
        void findByUserId_ExistingUser_ShouldReturnWorkspace() {
            Optional<Workspace> result = workspaceRepository.findByUserId(USER_ID);

            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isEqualTo(USER_ID);
            assertThat(result.get().getName()).isEqualTo("My Workspace");
        }

        @Test
        @DisplayName("Success: không tìm thấy workspace, trả về Optional rỗng")
        void findByUserId_NonExistingUser_ShouldReturnEmpty() {
            Optional<Workspace> result = workspaceRepository.findByUserId("non-existing-user");

            assertThat(result).isEmpty();
        }
    }

    private Workspace buildWorkspace(String userId, String name) {
        return Workspace.builder().userId(userId).name(name).build();
    }
}
