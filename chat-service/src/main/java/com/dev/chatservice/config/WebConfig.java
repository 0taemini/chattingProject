package com.dev.chatservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                    // 모든 엔드포인트
                .allowedOrigins("http://localhost:3000")  // 명시적 허용 origin
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);                 // 쿠키·인증 헤더 허용
    }
}

