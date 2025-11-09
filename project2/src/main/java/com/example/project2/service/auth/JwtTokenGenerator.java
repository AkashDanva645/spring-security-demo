package com.example.project2.service.auth;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


/**
 * Service responsible ONLY for generating tokens (access + refresh).
 * Put this in JwtTokenGenerator.java
 */
@Service
public class JwtTokenGenerator {

    @Value("${jwt.secret}")
    private String base64Secret;

    @Value("${jwt.access-token-validity-ms:900000}")
    private long accessTokenValidityMs; // default 15 min

    @Value("${jwt.refresh-token-validity-ms:1209600000}")
    private long refreshTokenValidityMs; // default 14 days

    private Key signingKey;

    @PostConstruct
    public void init() {
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalStateException("JWT secret must be provided in application properties (jwt.secret)");
        }
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(accessTokenValidityMs);
        String authorities = authoritiesToString(userDetails.getAuthorities());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities", authorities)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateRefreshToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(refreshTokenValidityMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String authoritiesToString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}