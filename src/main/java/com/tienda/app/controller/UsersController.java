package com.tienda.app.controller;
import java.util.List;
import com.tienda.app.dto.*;
import com.tienda.app.enums.RoleEnum;
import com.tienda.app.security.RequiresRole;
import com.tienda.app.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    @GetMapping("/me")
    @RequiresRole({RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR})
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> me(HttpServletRequest request) {
        return ResponseEntity.ok(usersService.getMe((Integer) request.getAttribute("userId")));
    }
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<List<UserDTO>>> all() {
        return ResponseEntity.ok(usersService.getAll());
    }
    @PostMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> create(@RequestBody CreateUserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.create(request));
    }
    @PatchMapping("/{id}/role")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> role(@PathVariable Integer id, @RequestBody RoleRequestDTO request) {
        return ResponseEntity.ok(usersService.changeRole(id, request.getRolId()));
    }
    @PatchMapping("/{id}/status")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> status(@PathVariable Integer id, @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(usersService.changeStatus(id, request.getActive()));
    }
}