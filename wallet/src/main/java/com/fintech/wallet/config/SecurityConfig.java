package com.fintech.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (Cross-Site Request Forgery) protection so Swagger can send POST/PUT requests
            .csrf(csrf -> csrf.disable())
            
            // 2. Define the exact road access rules
            .authorizeHttpRequests(auth -> auth
                // Whitelist Swagger UI and the API documentation files so they load perfectly
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Demand a password for ANY actual API data request
                .requestMatchers("/api/**").authenticated()
                
                // Catch-all: lock everything else just in case
                .anyRequest().authenticated()
            )
            
            // 3. Use standard Basic Authentication (the popup box) for the locked roads
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}