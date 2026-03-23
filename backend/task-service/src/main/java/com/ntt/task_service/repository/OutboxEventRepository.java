package com.ntt.task_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntt.task_service.domain.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus status, int maxRetry);
}
