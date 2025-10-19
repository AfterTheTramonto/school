package ru.hogwarts.school.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hogwarts School API",
                description = "API для управления студентами и факультетами школы Хогвартс",
                version = "1.0.0"
        )
)
public class OpenApiConfig {
}