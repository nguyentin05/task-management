package com.ntt.profile_service.consumer;

import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.service.ProfileService;
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
public class UserProfileConsumer {
    ProfileService userProfileService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "profile_queue", durable = "true"),
        exchange = @Exchange(value = "nttExchange", type = ExchangeTypes.TOPIC),
        key = "profile.created"
    ))
    public void handleProfileCreation(ProfileCreationRequest request) {
        try {
            userProfileService.create(request);
        } catch (Exception e) {
            log.error("Failed to create profile for userId={}: {}",
                    request.getUserId(), e.getMessage(), e);
        }
    }
}
