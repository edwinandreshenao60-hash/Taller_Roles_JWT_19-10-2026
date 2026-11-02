package com.tienda.app.controller;
import java.util.List;
import com.tienda.app.dto.*;
import com.tienda.app.enums.RoleEnum;
import com.tienda.app.security.RequiresRole;
import com.tienda.app.service.ProductsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;
    @GetMapping
    public ResponseEntity<HttpGlobalResponseDTO<List<ProductDTO>>> catalog(@RequestParam(required = false) Integer categoryId) {
        return ResponseEntity.ok(productsService.catalog(categoryId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> get(@PathVariable Integer id) {
        return ResponseEntity.ok(productsService.get(id));
    }
    @GetMapping("/mine")
    @RequiresRole(RoleEnum.VENDEDOR)
    public ResponseEntity<HttpGlobalResponseDTO<List<ProductDTO>>> mine(HttpServletRequest request) {
        return ResponseEntity.ok(productsService.mine((Integer) request.getAttribute("userId")));
    }
    @PostMapping
    @RequiresRole(RoleEnum.VENDEDOR)
    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> create(@RequestBody ProductRequestDTO request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.CREATED).body(productsService.create(request, userId));
    }
    @PutMapping("/{id}")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> update(@PathVariable Integer id, @RequestBody ProductRequestDTO request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        Integer roleId = (Integer) httpRequest.getAttribute("rolId");
        return ResponseEntity.ok(productsService.update(id, request, userId, roleId));
    }
    @DeleteMapping("/{id}")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<Void>> delete(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer roleId = (Integer) request.getAttribute("rolId");
        return ResponseEntity.ok(productsService.delete(id, userId, roleId));
    }
}