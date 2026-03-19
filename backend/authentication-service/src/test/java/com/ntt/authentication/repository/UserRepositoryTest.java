package com.ntt.authentication.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.ntt.authentication.domain.User;

@DataJpaTest
@TestPropertySource(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
        })
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build());
    }

    @Nested
    @DisplayName("existsByEmail: test hàm existsByEmail")
    class ExistsByEmailTest {

        @Test
        @DisplayName("Success: email tồn tại, trả về true")
        void existsByEmail_ExistingEmail_ShouldReturnTrue() {
            assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        }

        @Test
        @DisplayName("Success: email không tồn tại, trả về false")
        void existsByEmail_NonExistingEmail_ShouldReturnFalse() {
            assertThat(userRepository.existsByEmail("notfound@example.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("findByEmail: test hàm findByEmail")
    class FindByEmailTest {

        @Test
        @DisplayName("Success: email tồn tại, trả về User")
        void findByEmail_ExistingEmail_ShouldReturnUser() {
            Optional<User> result = userRepository.findByEmail("test@example.com");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Success: email không tồn tại, trả về empty")
        void findByEmail_NonExistingEmail_ShouldReturnEmpty() {
            Optional<User> result = userRepository.findByEmail("notfound@example.com");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findTop10ByEmailContainingIgnoreCase: test hàm tìm kiếm email")
    class FindTop10ByEmailContainingIgnoreCaseTest {

        @BeforeEach
        void setUp() {
            userRepository.saveAll(List.of(
                    User.builder().email("TEST2@example.com").password("pass").build(),
                    User.builder().email("another@gmail.com").password("pass").build(),
                    User.builder().email("test3@example.com").password("pass").build()));
        }

        @Test
        @DisplayName("Success: tìm kiếm case-insensitive, trả về đúng danh sách")
        void findTop10_CaseInsensitive_ShouldReturnMatches() {
            List<User> result = userRepository.findTop10ByEmailContainingIgnoreCase("test");

            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(User::getEmail)
                    .containsExactlyInAnyOrder("test@example.com", "TEST2@example.com", "test3@example.com");
        }

        @Test
        @DisplayName("Success: keyword không khớp, trả về danh sách rỗng")
        void findTop10_NoMatch_ShouldReturnEmptyList() {
            List<User> result = userRepository.findTop10ByEmailContainingIgnoreCase("xyz");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Success: có hơn 10 kết quả, chỉ trả về tối đa 10")
        void findTop10_MoreThan10Results_ShouldReturnMax10() {
            // setUp global: 1 + setUp nested: 3 + thêm 8 = 12 user có "test"
            for (int i = 4; i <= 11; i++) {
                userRepository.save(User.builder()
                        .email("test" + i + "@example.com")
                        .password("pass")
                        .build());
            }

            List<User> result = userRepository.findTop10ByEmailContainingIgnoreCase("test");

            assertThat(result).hasSize(10);
        }
    }
}
