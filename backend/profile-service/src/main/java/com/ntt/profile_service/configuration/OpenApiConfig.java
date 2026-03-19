package com.ntt.profile_service.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Task Service API", version = "1.0", description = "Tài liệu API cho phân hệ profile"),
        security = @SecurityRequirement(name = "bearerAuth") // Áp dụng ổ khóa cho toàn bộ API
        )
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {}
