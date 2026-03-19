package com.ntt.task_service.consumer;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.service.WorkspaceService;

import event.dto.UserCreatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WorkspaceConsumer {
    WorkspaceService workspaceService;

    @RabbitListener(
            bindings =
                    @QueueBinding(
                            value = @Queue(value = "workspace_queue", durable = "true"),
                            exchange = @Exchange(value = "nttExchange", type = ExchangeTypes.TOPIC),
                            key = "user.created"))
    public void handleWorkspaceCreation(UserCreatedEvent event) {
        try {
            WorkspaceCreationRequest request = WorkspaceCreationRequest.builder()
                    .userId(event.getUserId())
                    .name(event.getFirstName())
                    .description("Không gian làm việc của " + event.getFirstName())
                    .build();

            workspaceService.create(request);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý event tạo workspace cho userId: {}: {}", event.getUserId(), e.getMessage(), e);
        }
    }
}
