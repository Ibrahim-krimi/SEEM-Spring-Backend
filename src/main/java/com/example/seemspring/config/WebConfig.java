package com.example.seemspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Applique CORS aux routes correspondant à /api/**
                .allowedOrigins("http://localhost:4200") // Autorise les requêtes provenant de localhost:4200
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                .allowedHeaders("*") // Autorise tous les en-têtes
                .allowCredentials(true); // Permet les cookies ou les sessions
    }
}
