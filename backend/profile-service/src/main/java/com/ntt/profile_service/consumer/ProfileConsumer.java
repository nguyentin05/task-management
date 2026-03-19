package com.ntt.profile_service.consumer;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.service.ProfileService;

import event.dto.UserCreatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileConsumer {
    ProfileService profileService;

    @RabbitListener(
            bindings =
                    @QueueBinding(
                            value = @Queue(value = "profile_queue", durable = "true"),
                            exchange = @Exchange(value = "nttExchange", type = ExchangeTypes.TOPIC),
                            key = "user.created"))
    public void handleProfileCreation(UserCreatedEvent event) {
        try {
            ProfileCreationRequest request = ProfileCreationRequest.builder()
                    .userId(event.getUserId())
                    .firstName(event.getFirstName())
                    .lastName(event.getFirstName())
                    .build();

            profileService.create(request);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý event tạo profile cho userId: {}: {}", event.getUserId(), e.getMessage(), e);
        }
    }
}
