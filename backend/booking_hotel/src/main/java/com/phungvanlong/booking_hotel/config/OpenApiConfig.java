package com.phungvanlong.booking_hotel.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Aura Resort Management & Booking API")
                        .description("Tài liệu đặc tả toàn bộ RESTful API cho Aura Resort: Đặt phòng, Quản trị Buồng phòng, Kiểm kho, Phân công nhân sự và Cổng thanh toán.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aura Resort Engineering Team")
                                .email("contact@auraresort.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("/api/v1").description("Aura Resort API Server")
                ))
                // Cấu hình Bearer Token JWT để test trực tiếp trên Swagger UI
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Nhập Access Token (JWT) được trả về từ API /auth/login")));
    }
}
