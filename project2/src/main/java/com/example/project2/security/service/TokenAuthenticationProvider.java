package com.example.project2.security.service;


import com.example.project2.security.dto.CustomAuthenticationException;
import com.example.project2.security.dto.TokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.List;


public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtTokenDecoder jwtTokenDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TokenAuthentication auth = (TokenAuthentication) authentication;
        String token = auth.getCredentials();

        String username = jwtTokenDecoder.getUsernameFromToken(token);
        List<GrantedAuthority> grantedAuthorityList = jwtTokenDecoder.getAuthoritiesFromToken(token);

        if (!StringUtils.hasLength(username)) {
            throw new CustomAuthenticationException("Invalid Token.");
        }

        return new TokenAuthentication(username, grantedAuthorityList);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthentication.class.equals(authentication);
    }
}
