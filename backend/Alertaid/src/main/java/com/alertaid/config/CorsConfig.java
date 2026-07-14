package com.alertaid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:8080,http://127.0.0.1:8080,http://localhost:3000}")
    private String allowedOrigins;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials for authentication
        config.setAllowCredentials(true);

        // Set allowed origins based on environment
        List<String> originPatterns = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
        // Allow file:// origins (they show up as "null") so standalone HTML pages can hit the API
        if (originPatterns.stream().noneMatch("null"::equalsIgnoreCase)) {
            originPatterns.add("null");
        }
        // Allow any localhost/127.0.0.1 port plus common hybrid app schemes
        originPatterns.addAll(List.of(
                "http://localhost:*",
                "https://localhost:*",
                "http://127.0.0.1:*",
                "https://127.0.0.1:*",
                "http://192.168.*.*:*",
                "https://192.168.*.*:*",
                "capacitor://localhost",
                "ionic://localhost"
        ));
        config.setAllowedOriginPatterns(originPatterns);

        // Allowed HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Specific headers instead of wildcard for better security
        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Cache-Control",
            "X-File-Name"
        ));

        // Headers that can be exposed to the client
        config.setExposedHeaders(List.of(
            "Authorization",
            "Content-Disposition",
            "X-Total-Count"
        ));

        // Cache preflight requests for better performance
        config.setMaxAge(3600L); // 1 hour

        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
