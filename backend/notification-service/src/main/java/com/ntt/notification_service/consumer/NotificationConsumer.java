package com.ntt.notification_service.consumer;

import com.ntt.notification_service.dto.request.Recipient;
import com.ntt.notification_service.dto.request.SendEmailRequest;
import com.ntt.notification_service.service.EmailService;
import event.dto.UserCreatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationConsumer {
    EmailService emailService;

    @RabbitListener(
            bindings =
            @QueueBinding(
                    value = @Queue(value = "notifitcation_queue", durable = "true"),
                    exchange = @Exchange(value = "nttExchange", type = ExchangeTypes.TOPIC),
                    key = "user.created"))
    public void handleWorkspaceCreation(UserCreatedEvent event) {
        try {
            emailService.sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(event.getEmail())
                            .build())
                    .subject("Chào mừng bạn đến với Task Management!")
                    .htmlContent("""
                            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                                <h2 style="color: #4A90E2;">Chào mừng %s %s!</h2>
                                <p>Tài khoản của bạn đã được tạo thành công.</p>
                                <p>Email đăng nhập: <strong>%s</strong></p>
                                <p>Hãy bắt đầu quản lý công việc của bạn ngay hôm nay!</p>
                                <br>
                                <p style="color: #888; font-size: 12px;">Đây là email tự động, vui lòng không reply.</p>
                            </div>
                            """
                            .formatted(event.getFirstName(), event.getLastName(), event.getEmail()))
                    .build());

        } catch (Exception e) {
            log.error(
                    "[Notification][Consumer]Lỗi khi xử lý event gửi mail chào mừng cho userId: {}: {}",
                    event.getUserId(),
                    e.getMessage(),
                    e);
        }
    }
}
