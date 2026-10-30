package com.tienda.app.controller;
import java.util.List;
import com.tienda.app.dto.*;
import com.tienda.app.enums.RoleEnum;
import com.tienda.app.security.RequiresRole;
import com.tienda.app.service.CategoriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoriesService categoriesService;
    @GetMapping
    @RequiresRole({RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR})
    public ResponseEntity<HttpGlobalResponseDTO<List<CategoryDTO>>> all() {
        return ResponseEntity.ok(categoriesService.getAll());
    }
    @PostMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<CategoryDTO>> create(@RequestBody CategoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriesService.create(request));
    }
    @DeleteMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriesService.delete(id));
    }
}