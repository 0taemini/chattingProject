//package com.dev.chatservice.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    // WebsocketConfig에서 CORS 설정했어도 해결안되면
//    // 이거를 작성
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")                    // 모든 엔드포인트
//                .allowedOrigins("*")  // 명시적 허용 origin
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowCredentials(true);                 // 쿠키·인증 헤더 허용
//    }
//}
//
