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
        List<OutboxEvent> pendingMessages =
                outboxMessageRepository.findByStatusAndRetryCountLessThan(OutboxEvent.OutboxStatus.PENDING, MAX_RETRY);

        for (OutboxEvent message : pendingMessages) {
            try {
                rabbitMQProducer.sendMessage(message.getRoutingKey(), message.getPayload());
                outboxMessageRepository.delete(message);
            } catch (Exception e) {
                message.setRetryCount(message.getRetryCount() + 1);

                if (message.getRetryCount() >= MAX_RETRY) {
                    message.setStatus(OutboxEvent.OutboxStatus.FAILED);
                    log.error(
                            "Outbox event lỗi sau {} lần thử: id={}, routingKey={}",
                            MAX_RETRY,
                            message.getId(),
                            message.getRoutingKey());
                }
            }
            outboxMessageRepository.save(message);
        }
    }
}
