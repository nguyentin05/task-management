package com.ntt.profile_service.consumer;

import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.service.UserProfileService;
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
public class UserProfileConsumer {
    UserProfileService userProfileService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "profile_queue", durable = "true"),
            exchange = @Exchange(value = "nttExchange", type = ExchangeTypes.TOPIC),
            key = "profile.created"
    ))
    public void handleProfileCreation(ProfileCreationRequest request) {
        userProfileService.createProfile(request);
    }
}
