package com.example.project2.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public class AppException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
