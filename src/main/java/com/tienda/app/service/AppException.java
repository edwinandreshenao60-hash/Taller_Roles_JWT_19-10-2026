package com.tienda.app.service;
import org.springframework.http.HttpStatus;
import lombok.Getter;
@Getter
public class AppException extends RuntimeException {
    private final HttpStatus status;
    public AppException(HttpStatus status, String message) { super(message); this.status = status; }
}