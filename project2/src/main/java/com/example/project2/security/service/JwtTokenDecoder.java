package com.example.project2.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service responsible ONLY for decoding and validating tokens.
 * Put this in JwtTokenDecoder.java
 */
@Service
public class JwtTokenDecoder {

    @Value("${jwt.secret}")
    private String base64Secret;

    private Key signingKey;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalStateException("JWT secret must be provided in application properties (jwt.secret)");
        }
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
                .setSigningKey(signingKey)
                .build();
    }

    /**
     * Validate token signature and expiration.
     * Returns true if token is valid; false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // In production log the exception details (type, cause) for monitoring
            return false;
        }
    }

    /**
     * Parse claims and return the subject (username).
     * Throws runtime exception if token is invalid.
     */
    public String getUsernameFromToken(String token) {
        Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        return jws.getBody().getSubject();
    }

    /**
     * Get a claim by name as String; returns null if not present.
     */
    public String getStringClaim(String token, String claimName) {
        Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        Object claim = jws.getBody().get(claimName);
        return claim == null ? null : claim.toString();
    }

    /**
     * Convenience to return roles claim as CSV string (or empty string if missing)
     */
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        String authorities = getStringClaim(token, "authorities");
        return Arrays.stream(authorities.split(";")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * Parse full Claims object (useful if caller wants issuedAt, expiration etc.)
     */
    public Claims getAllClaims(String token) {
        Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        return jws.getBody();
    }
}
