package com.example.project2.security.service;

import com.example.project2.security.dto.CustomAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


public class CustomAuthenticationManager implements AuthenticationManager {

    private List<AuthenticationProvider> authenticationProviderList = new ArrayList<>();

    public CustomAuthenticationManager(List<AuthenticationProvider> authenticationProviderList) {
        this.authenticationProviderList = authenticationProviderList;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        for (AuthenticationProvider authenticationProvider: authenticationProviderList) {
            if (authenticationProvider.supports(authentication.getClass())) {
                return authenticationProvider.authenticate(authentication);
            }
        }
        throw new IllegalArgumentException("No Such Authentication Method.");
    }
}
