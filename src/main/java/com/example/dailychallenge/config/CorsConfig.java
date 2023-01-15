package com.example.dailychallenge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Slf4j
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true); // json 서버 응답을 자바스크립트에서 처리할 수 있게 해줌
        corsConfiguration.addAllowedOrigin("*"); // 모든 ip에 응답을 허용
        corsConfiguration.addAllowedMethod("*"); // 모든 HTTP METHOD에 허용
        corsConfiguration.addAllowedHeader("*"); // 모든 HTTP HEADER에 허용
        source.registerCorsConfiguration("/**", corsConfiguration);
        log.info("2.");

        return new CorsFilter(source);
    }
}
