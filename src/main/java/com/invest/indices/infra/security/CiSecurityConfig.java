package com.invest.indices.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Profile("ci")
@Configuration
public class CiSecurityConfig {

    @Bean
    SecurityFilterChain ciSecurity(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable()).authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**"
                ).permitAll().anyRequest().denyAll()
        );
        return http.build();
    }
}
