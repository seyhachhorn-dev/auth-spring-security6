package com.seyha.authentication.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

/**
 * Developed by ChhornSeyha
 * Date: 16/02/2026
 */

@Configuration
public class JacksonConfig  {
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
