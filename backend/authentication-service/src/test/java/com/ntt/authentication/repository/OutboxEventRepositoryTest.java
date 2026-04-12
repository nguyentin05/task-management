package com.ntt.authentication.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.ntt.authentication.domain.OutboxEvent;

@DataJpaTest
@ActiveProfiles("test")
class OutboxEventRepositoryTest {

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @BeforeEach
    void setUp() {
        outboxEventRepository.saveAll(List.of(
                buildEvent(OutboxEvent.OutboxStatus.PENDING, 0),
                buildEvent(OutboxEvent.OutboxStatus.PENDING, 2),
                buildEvent(OutboxEvent.OutboxStatus.PENDING, 3),
                buildEvent(OutboxEvent.OutboxStatus.FAILED, 1)));
    }

    @Nested
    @DisplayName("findByStatus: tìm event theo status")
    class FindByStatusTest {

        @Test
        @DisplayName("Success: lọc đúng theo status PENDING, trả về tất cả PENDING")
        void findByStatus_PendingStatus_ShouldReturnAllPending() {
            List<OutboxEvent> result =
                    outboxEventRepository.findByStatus(OutboxEvent.OutboxStatus.PENDING);
            assertThat(result).hasSize(3);
            assertThat(result).extracting(OutboxEvent::getRetryCount)
                    .containsExactlyInAnyOrder(0, 2, 3);
        }

        @Test
        @DisplayName("Success: không có event PENDING, trả về danh sách rỗng")
        void findByStatus_NoPending_ShouldReturnEmptyList() {
            outboxEventRepository.deleteAll();
            outboxEventRepository.save(buildEvent(OutboxEvent.OutboxStatus.FAILED, 1));

            List<OutboxEvent> result =
                    outboxEventRepository.findByStatus(OutboxEvent.OutboxStatus.PENDING);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Success: lọc đúng khi status là FAILED")
        void findByStatus_FailedStatus_ShouldReturnCorrectly() {
            List<OutboxEvent> result =
                    outboxEventRepository.findByStatus(OutboxEvent.OutboxStatus.FAILED);
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus()).isEqualTo(OutboxEvent.OutboxStatus.FAILED);
        }
    }

    private OutboxEvent buildEvent(OutboxEvent.OutboxStatus status, int retryCount) {
        return OutboxEvent.builder()
                .routingKey("user.created")
                .payload("{}")
                .status(status)
                .retryCount(retryCount)
                .createdAt(Instant.now())
                .build();
    }
}
