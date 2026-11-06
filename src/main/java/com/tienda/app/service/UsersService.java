package com.tienda.app.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.app.dto.*;
import com.tienda.app.entity.Roles;
import com.tienda.app.entity.Users;
import com.tienda.app.repository.RolesRepository;
import com.tienda.app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public HttpGlobalResponseDTO<UserDTO> getMe(Integer userId) {
        Users user = findUser(userId);
        return authService.wrapUser(user, "Perfil encontrado");
    }

    public HttpGlobalResponseDTO<List<UserDTO>> getAll() {
        List<UserDTO> data = new ArrayList<>();
        for (Users user : usersRepository.findAll()) data.add(authService.toUserDTO(user));
        return new HttpGlobalResponseDTO<>("Usuarios encontrados", data);
    }

    public HttpGlobalResponseDTO<UserDTO> create(CreateUserRequestDTO request) {
        if (request == null) throw new AppException(HttpStatus.BAD_REQUEST, "Faltan los datos del usuario");
        if (request.getUsername() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El username es obligatorio");
        if (request.getEmail() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        if (request.getPassword() == null) throw new AppException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        if (usersRepository.existsByUsername(request.getUsername())) throw new AppException(HttpStatus.CONFLICT, "Username ya registrado");
        if (usersRepository.existsByEmail(request.getEmail())) throw new AppException(HttpStatus.CONFLICT, "Email ya registrado");
        Roles role = rolesRepository.findById(request.getRolId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "El rol no existe"));
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setActive(true);
        return authService.wrapUser(usersRepository.save(user), "Usuario creado");
    }

    @Transactional
    public HttpGlobalResponseDTO<UserDTO> changeRole(Integer id, Integer roleId) {
        Users user = findUser(id);
        if (roleId == null) throw new AppException(HttpStatus.BAD_REQUEST, "rolId es obligatorio");
        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "El rol no existe"));
        user.setRole(role);
        return authService.wrapUser(user, "Rol actualizado");
    }

    @Transactional
    public HttpGlobalResponseDTO<UserDTO> changeStatus(Integer id, Boolean active) {
        if (active == null) throw new AppException(HttpStatus.BAD_REQUEST, "El estado active es obligatorio");
        Users user = findUser(id);
        user.setActive(active);
        return authService.wrapUser(user, "Estado actualizado");
    }

    private Users findUser(Integer id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}