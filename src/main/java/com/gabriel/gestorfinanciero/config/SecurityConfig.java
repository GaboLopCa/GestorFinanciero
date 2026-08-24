package com.gabriel.gestorfinanciero.config;

import com.gabriel.gestorfinanciero.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Desactivamos CSRF por ser API REST stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No guarda sesiones
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Rutas de Login y Registro serán públicas
                        .anyRequest().authenticated()            // Todo lo demás exige estar autenticado
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Integramos nuestro JwtFilter
                .build();
    }
}