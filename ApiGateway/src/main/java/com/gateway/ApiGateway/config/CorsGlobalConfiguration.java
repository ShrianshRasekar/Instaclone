package com.gateway.ApiGateway.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Only allow specific origin
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Exactly this, no *
        
        // Allow any HTTP method (GET, POST, PUT, etc.)
        corsConfig.addAllowedMethod("*"); 
        
        // Allow any headers
        corsConfig.addAllowedHeader("*");

        // Allow credentials (cookies, tokens)
        corsConfig.setAllowCredentials(true); 

        // Apply CORS config to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}

