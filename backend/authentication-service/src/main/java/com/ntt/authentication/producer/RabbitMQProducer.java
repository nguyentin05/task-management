package com.ntt.authentication.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ntt.authentication.configuration.RabbitMQConfig;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMQProducer {
    RabbitTemplate rabbitTemplate;
    ObjectMapper objectMapper;

    public void sendEvent(String routingKey, String payload) {
        try {
            Object event = objectMapper.readValue(payload, Object.class);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
        } catch (Exception e) {
            log.error("Bắn sự kiện thất bại: {}", e.getMessage(), e);
        }
    }
}
