package com.ntt.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntt.authentication.domain.OutboxEvent;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus status, int maxRetry);
}
