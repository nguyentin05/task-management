package com.ntt.authentication.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;
import com.ntt.authentication.dto.request.AuthenticationRequest;
import com.ntt.authentication.dto.request.TokenIntrospectRequest;
import com.ntt.authentication.dto.request.UserRegisterRequest;
import com.ntt.authentication.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private ConnectionFactory connectionFactory;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testEndToEndAuthenticationFlow() throws Exception {
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .email("test.integration@example.com")
                .password("Password@123")
                .firstName("Integration")
                .lastName("Test")
                .build();

        mockMvc.perform(post("/auth/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value("test.integration@example.com"));

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("test.integration@example.com")
                .password("Password@123")
                .build();

        MvcResult result = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token").exists())
                .andExpect(jsonPath("$.result.authenticated").value(true))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        String token = JsonPath.read(responseString, "$.result.token");

        TokenIntrospectRequest introspectRequest =
                TokenIntrospectRequest.builder().token(token).build();

        mockMvc.perform(post("/internal/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(introspectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.valid").value(true));
    }
}
