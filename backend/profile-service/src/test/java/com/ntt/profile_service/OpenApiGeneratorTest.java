package com.ntt.profile_service;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiGeneratorTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private ConnectionFactory connectionFactory;

    @Test
    void generateOpenApiYaml() throws Exception {
        String yamlContent = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Files.createDirectories(Paths.get("target/openapi"));

        Files.writeString(Paths.get("target/openapi/profile-service.yaml"), yamlContent);
    }
}
