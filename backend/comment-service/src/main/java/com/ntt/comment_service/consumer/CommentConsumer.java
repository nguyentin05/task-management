package com.ntt.comment_service.consumer;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ntt.comment_service.service.CommentService;

import event.dto.TaskDeletedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentConsumer {
    CommentService commentService;

    @RabbitListener(
            bindings =
                    @QueueBinding(
                            value = @Queue(value = "comment_queue", durable = "true"),
                            exchange = @Exchange(value = "nttExchange", type = ExchangeTypes.TOPIC),
                            key = "task.deleted"))
    public void handleTaskDeleted(TaskDeletedEvent event) {
        try {
            commentService.deleteCommentByTask(event.getTaskId());
        } catch (Exception e) {
            log.error(
                    "[Comment][Consumer]Lỗi khi xử lý event xóa comment cho taskId: {}: {}",
                    event.getTaskId(),
                    e.getMessage(),
                    e);
        }
    }
}
