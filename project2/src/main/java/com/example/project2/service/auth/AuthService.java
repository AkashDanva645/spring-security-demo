package com.example.project2.service.auth;


import com.example.project2.domain.exception.AppException;
import com.example.project2.dto.request.LoginRequest;
import com.example.project2.repository.UserDao;
import com.example.project2.security.service.JwtTokenDecoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private JwtTokenDecoder jwtTokenDecoder;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public String login(LoginRequest reqDto, HttpServletResponse response) {
        UserDetails user = userDao.fetchByUsername(reqDto.getUsername());
        if (user == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid Email.");
        }
        if (!user.getPassword().equals(reqDto.getPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid Password.");
        }

        String accessToken = jwtTokenGenerator.generateAccessToken(user);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(user);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                //.secure(true)
                .maxAge(1209600 - 100)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return accessToken;
    }


    public String refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie refreshTokenCookie = null;
        for (Cookie cookie: request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                refreshTokenCookie = cookie;
                break;
            }
        }

        if (refreshTokenCookie == null) {
            throw new AccessDeniedException("Invalid Token. Please Login Again");
        }

        String username = jwtTokenDecoder.getUsernameFromToken(refreshTokenCookie.getValue());

        UserDetails user = userDao.fetchByUsername(username);
        if (user == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid Email.");
        }

        String accessToken = jwtTokenGenerator.generateAccessToken(user);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(user);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                //.secure(true)
                .maxAge(1209600 - 100)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return accessToken;
    }

}
