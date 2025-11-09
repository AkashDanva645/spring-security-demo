package com.example.project2.security.dto;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class TokenAuthentication implements Authentication {

    private String token;

    private String email;

    private List<GrantedAuthority> grantedAuthorityList;

    private boolean isAuthenticated;

    public TokenAuthentication(String token) {
        this.token = token;
        this.isAuthenticated = false;
    }

    public TokenAuthentication(String email, List<GrantedAuthority> grantedAuthorityList) {
        this.email = email;
        this.grantedAuthorityList = grantedAuthorityList;
        this.isAuthenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorityList;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return email;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (email == null && isAuthenticated) throw new IllegalArgumentException("Can't be authenticated without Principal.");
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return this.email;
    }
}
