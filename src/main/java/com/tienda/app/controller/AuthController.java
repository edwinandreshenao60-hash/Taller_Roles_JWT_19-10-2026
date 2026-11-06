package com.tienda.app.controller;
import com.tienda.app.dto.*;
import com.tienda.app.service.AuthService;
import com.tienda.app.service.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> refresh(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) throw new AppException(HttpStatus.UNAUTHORIZED, "Falta el token");
        if (!header.startsWith("Bearer ")) throw new AppException(HttpStatus.UNAUTHORIZED, "El token debe usar Bearer");
        return ResponseEntity.ok(authService.refresh(header.substring(7)));
    }
}