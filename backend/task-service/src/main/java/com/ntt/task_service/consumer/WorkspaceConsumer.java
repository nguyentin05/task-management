package com.ntt.task_service.consumer;

import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.service.WorkspaceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkspaceConsumer {
    WorkspaceService workspaceService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "profile_queue", durable = "true"),
            exchange = @Exchange(value = "nttExchange", type = ExchangeTypes.TOPIC),
            key = "workspace.created"
    ))
    public void handleProfileCreation(WorkspaceCreationRequest request) {
        workspaceService.createWorkspace(request);
    }
}
