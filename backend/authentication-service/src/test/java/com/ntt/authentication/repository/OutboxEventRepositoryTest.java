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
import org.springframework.test.context.TestPropertySource;

import com.ntt.authentication.domain.OutboxEvent;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yaml")
class OutboxEventRepositoryTest {

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @BeforeEach
    void setUp() {
        outboxEventRepository.saveAll(List.of(
                buildEvent(OutboxEvent.OutboxStatus.PENDING, 0),
                buildEvent(OutboxEvent.OutboxStatus.PENDING, 2),
                buildEvent(OutboxEvent.OutboxStatus.PENDING, 3), // retryCount = maxRet
                buildEvent(OutboxEvent.OutboxStatus.FAILED, 1))); // status khác → không lấy
    }

    @Nested
    @DisplayName("findByStatusAndRetryCountLessThan: test hàm tìm event cần retry")
    class FindByStatusAndRetryCountLessThanTest {

        @Test
        @DisplayName("Success: lọc đúng theo status PENDING và retryCount < maxRetry")
        void findByStatusAndRetryCountLessThan_PendingStatus_ShouldReturnMatches() {
            // maxRetry = 3 → lấy PENDING có retryCount 0, 2 (không lấy retryCount = 3)
            List<OutboxEvent> result =
                    outboxEventRepository.findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus.PENDING, 3);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(OutboxEvent::getRetryCount).containsExactlyInAnyOrder(0, 2);
        }

        @Test
        @DisplayName("Success: không có event nào thỏa điều kiện, trả về danh sách rỗng")
        void findByStatusAndRetryCountLessThan_NoMatch_ShouldReturnEmptyList() {
            List<OutboxEvent> result =
                    outboxEventRepository.findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus.PENDING, 0);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Success: lọc đúng khi status là FAILED")
        void findByStatusAndRetryCountLessThan_FailedStatus_ShouldReturnCorrectly() {
            List<OutboxEvent> result =
                    outboxEventRepository.findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus.FAILED, 3);

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
