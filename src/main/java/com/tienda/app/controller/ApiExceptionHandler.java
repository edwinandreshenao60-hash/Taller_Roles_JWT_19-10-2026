package com.tienda.app.controller;
import com.tienda.app.dto.HttpGlobalResponseDTO;
import com.tienda.app.service.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handle(AppException exception) {
        HttpGlobalResponseDTO<Void> body = new HttpGlobalResponseDTO<>(exception.getMessage(), null);
        return ResponseEntity.status(exception.getStatus()).body(body);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handleInvalidBody() {
        HttpGlobalResponseDTO<Void> body = new HttpGlobalResponseDTO<>("El cuerpo de la solicitud no es válido", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}