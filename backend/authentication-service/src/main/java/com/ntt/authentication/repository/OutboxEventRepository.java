package com.ntt.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntt.authentication.domain.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus status, int maxRetry);
}
