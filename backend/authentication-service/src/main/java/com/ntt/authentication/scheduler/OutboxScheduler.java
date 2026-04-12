package com.ntt.authentication.scheduler;

import java.util.List;

import org.springframework.amqp.AmqpException;
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
        List<OutboxEvent> pendingEvents = outboxMessageRepository.findByStatus(OutboxEvent.OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                rabbitMQProducer.sendEvent(event.getRoutingKey(), event.getPayload());
                outboxMessageRepository.delete(event);
            } catch (AmqpException e) {
                if (event.getRetryCount() < MAX_RETRY) {
                    event.setRetryCount(event.getRetryCount() + 1);
                    outboxMessageRepository.save(event);
                }
                break;
            } catch (Exception e) {
                log.error(
                        "[Scheduler][Authentication] Event {} lỗi không thể retry: {}", event.getId(), e.getMessage());
                event.setStatus(OutboxEvent.OutboxStatus.FAILED);
                outboxMessageRepository.save(event);
            }
        }
    }
}
