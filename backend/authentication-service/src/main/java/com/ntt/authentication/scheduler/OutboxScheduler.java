package com.ntt.authentication.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.authentication.domain.OutboxEvent;
import com.ntt.authentication.producer.RabbitMQProducer;
import com.ntt.authentication.repository.OutboxEventRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboxScheduler {
    OutboxEventRepository outboxMessageRepository;
    RabbitMQProducer rabbitMQProducer;
    static final int MAX_RETRY = 3;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> pendingEvent =
                outboxMessageRepository.findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus.PENDING, MAX_RETRY);

        for (OutboxEvent event : pendingEvent) {
            try {
                rabbitMQProducer.sendEvent(event.getRoutingKey(), event.getPayload());
                outboxMessageRepository.delete(event);
            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);

                if (event.getRetryCount() >= MAX_RETRY) {
                    event.setStatus(OutboxEvent.OutboxStatus.FAILED);
                    log.error(
                            "Outbox event lỗi sau {} lần thử: id={}, routingKey={}",
                            MAX_RETRY,
                            event.getId(),
                            event.getRoutingKey());
                }
                outboxMessageRepository.save(event);
            }
        }
    }
}
