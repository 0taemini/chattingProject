package com.dev.chatservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 필터 활성화
                .cors(Customizer.withDefaults())
                // CSRF 끄기 (REST/API, WS 사용 시)
                .csrf(csrf -> csrf.disable())
                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Preflight 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 히스토리 조회, WS 핸드셰이크 허용
                        .requestMatchers("/api/chat/history/**").permitAll()
                        .requestMatchers("/ws-chat/**").permitAll()
                        // 나머지는 인증
                        .anyRequest().authenticated()
                )
                // 필요하면 HTTP Basic 또는 Form 로그인
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // 와일드카드 패턴으로 모든 origin 허용
        cfg.setAllowedOriginPatterns(List.of("*"));
        // 사전 요청 포함 메서드 허용
        cfg.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        cfg.setAllowedHeaders(List.of("*"));
        // 자격증명 허용
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // REST API와 SockJS 엔드포인트에 CORS 적용
        source.registerCorsConfiguration("/api/**", cfg);
        source.registerCorsConfiguration("/ws-chat/**", cfg);
        return source;
    }
}
