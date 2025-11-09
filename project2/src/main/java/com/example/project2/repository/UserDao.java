package com.example.project2.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDao {

    private final InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

    @PostConstruct
    public void fillDummyData() {
        userDetailsManager.createUser(
            User.builder()
                    .username("akashdanva@email.com")
                    .password("akash@123")
                    .authorities("SECURED_END_POINT")
                    .build()
        );
    }

    public UserDetails fetchByUsername(String username) {
        return userDetailsManager.loadUserByUsername(username);
    }
}
