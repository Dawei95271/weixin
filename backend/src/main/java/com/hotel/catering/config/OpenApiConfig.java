package com.hotel.catering.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
            .title("酒店二楼餐饮服务 API")
            .description("餐饮点单、客房扫码送餐、包间预约、宴席预约接口")
            .version("1.0.0"));
    }
}
