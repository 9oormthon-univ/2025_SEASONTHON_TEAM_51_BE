//// src/main/java/com/mockit/global/config/SecurityConfig.java
//package com.mockit.global.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@Profile("dev") // dev 프로필에서만 적용
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .headers(h -> h.frameOptions(f -> f.disable()))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/",                      // 루트 허용
//                                "/**",                   // 전부 허용(DEV)
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/swagger-resources/**",
//                                "/webjars/**"
//                        ).permitAll()
//                        .anyRequest().permitAll()
//                )
//                .formLogin(login -> login
//                        .loginPage("/login") // /login 을 로그인 페이지로 유지
//                        .permitAll()
//                )
//                .logout(Customizer.withDefaults());
//        return http.build();
//    }
//}