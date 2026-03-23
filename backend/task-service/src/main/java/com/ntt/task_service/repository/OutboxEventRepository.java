package com.ntt.task_service.repository;

import com.ntt.task_service.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus status, int maxRetry);
}
