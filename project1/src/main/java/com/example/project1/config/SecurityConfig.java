package com.example.project1.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(c -> c.disable());
        httpSecurity.authorizeHttpRequests(
            c -> c
                    .requestMatchers(HttpMethod.GET, "/").hasAuthority("READ")
                    .requestMatchers(HttpMethod.POST, "/").hasAuthority("WRITE")
                    .anyRequest().authenticated()
        );
        httpSecurity.httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User.builder()
                .username("Akash")
                .password(passwordEncoder().encode("akash@123"))
                .authorities(List.of(new SimpleGrantedAuthority("READ")))
                .build();

        UserDetails user2 = User.builder()
                .username("Aryan")
                .password(passwordEncoder().encode("aryan@123"))
                .authorities(List.of(new SimpleGrantedAuthority("WRITE")))
                .build();

        UserDetailsService uds = new InMemoryUserDetailsManager(List.of(user1, user2));
        return uds;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
