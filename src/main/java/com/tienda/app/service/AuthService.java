package com.tienda.app.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tienda.app.dto.*;
import com.tienda.app.entity.Roles;
import com.tienda.app.entity.Users;
import com.tienda.app.enums.RoleEnum;
import com.tienda.app.repository.RolesRepository;
import com.tienda.app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public HttpGlobalResponseDTO<UserDTO> register(RegisterRequestDTO request) {
        validateRegistration(request);
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new AppException(HttpStatus.CONFLICT, "El nombre de usuario ya está registrado");
        }
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Roles clientRole = rolesRepository.findById(RoleEnum.CLIENTE.getId())
                .orElseThrow(() -> new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "No existe el rol CLIENTE"));
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(clientRole);
        user.setActive(true);
        user = usersRepository.save(user);
        return new HttpGlobalResponseDTO<>("Usuario registrado", toUserDTO(user));
    }

    public HttpGlobalResponseDTO<LoginResponseDTO> login(LoginRequestDTO request) {
        if (request == null) throw new AppException(HttpStatus.BAD_REQUEST, "Faltan los datos de acceso");
        if (request.getUser() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        if (request.getPassword() == null) throw new AppException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");

        Users user = usersRepository.findByUsername(request.getUser()).orElse(null);
        if (user == null) user = usersRepository.findByEmail(request.getUser()).orElse(null);
        if (user == null) throw new AppException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        if (!user.isActive()) throw new AppException(HttpStatus.UNAUTHORIZED, "El usuario está inactivo");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
        LoginResponseDTO data = new LoginResponseDTO(jwtService.generateToken(user), toUserDTO(user));
        return new HttpGlobalResponseDTO<>("Inicio de sesión exitoso", data);
    }

    public HttpGlobalResponseDTO<LoginResponseDTO> refresh(String token) {
        if (!jwtService.isTokenValid(token)) throw new AppException(HttpStatus.UNAUTHORIZED, "El token no es válido o expiró");
        Integer userId = jwtService.extractUserId(token);
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "El usuario ya no existe"));
        if (!user.isActive()) throw new AppException(HttpStatus.UNAUTHORIZED, "El usuario está inactivo");
        LoginResponseDTO data = new LoginResponseDTO(jwtService.generateToken(user), toUserDTO(user));
        return new HttpGlobalResponseDTO<>("Token renovado", data);
    }

    private void validateRegistration(RegisterRequestDTO request) {
        if (request == null) throw new AppException(HttpStatus.BAD_REQUEST, "Faltan los datos de registro");
        if (request.getUsername() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El username es obligatorio");
        if (request.getUsername().isBlank()) throw new AppException(HttpStatus.BAD_REQUEST, "El username es obligatorio");
        if (request.getEmail() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        if (request.getEmail().isBlank()) throw new AppException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        if (request.getPassword() == null) throw new AppException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        if (request.getPassword().isBlank()) throw new AppException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
    }

    public UserDTO toUserDTO(Users user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole().getName(), user.isActive());
    }

    public HttpGlobalResponseDTO<UserDTO> wrapUser(Users user, String message) {
        return new HttpGlobalResponseDTO<>(message, toUserDTO(user));
    }
}