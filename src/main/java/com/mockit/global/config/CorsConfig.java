//package com.mockit.global.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.List;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        // 모든 Origin 허용 (Swagger 테스트 포함)
//        config.setAllowedOriginPatterns(List.of("*"));
//
//        // 허용 메서드
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//
//        // 모든 헤더 허용
//        config.setAllowedHeaders(List.of("*"));
//
//        // 인증 정보(쿠키, Authorization 헤더) 허용
//        config.setAllowCredentials(true);
//
//        // 클라이언트에서 읽을 수 있는 헤더
//        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
//
//        // 모든 경로에 적용
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsFilter(source);
//    }
//}