package com.iscoolgpt.iscool_gpt_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IsCoolGPT - Assistente de Estudos em Cloud")
                        .version("v1.0.0")
                        .description("API RESTful completa para o assistente inteligente de estudos, usando LLM e arquitetura Cloud."));
    }
}