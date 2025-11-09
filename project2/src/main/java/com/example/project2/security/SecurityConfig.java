package com.example.project2.security;

import com.example.project2.security.service.CustomAuthenticationFilter;
import com.example.project2.security.service.CustomAuthenticationManager;
import com.example.project2.security.service.TokenAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/auth/**", "/csrf");

        http.csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        http.cors(c -> c.configurationSource(corsConfigurationSource()));

        http.authorizeHttpRequests(c -> c
                .requestMatchers("/auth/**", "/csrf").permitAll()
        );

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/api/**");

        http.csrf(c -> c.disable());

        http.cors(c -> c.configurationSource(corsConfigurationSource()));

        http.addFilterAt(new CustomAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(c -> c
                .anyRequest().authenticated()
        );

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080")); // Your frontend origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization")); // Optional
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authenticationProviderList = new ArrayList<>();
        authenticationProviderList.add(tokenAuthenticationProvider());
        return new CustomAuthenticationManager(authenticationProviderList);
    }

    @Bean
    @Qualifier("tokenAuthenticationProvider")
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider();
    }
}
