package com.tienda.app.controller;
import java.util.List;
import com.tienda.app.dto.*;
import com.tienda.app.enums.RoleEnum;
import com.tienda.app.security.RequiresRole;
import com.tienda.app.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;
    @PostMapping
    @RequiresRole(RoleEnum.CLIENTE)
    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> create(@RequestBody OrderRequestDTO request, HttpServletRequest httpRequest) {
        Integer clientId = (Integer) httpRequest.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.CREATED).body(ordersService.create(request, clientId));
    }
    @GetMapping("/mine")
    @RequiresRole(RoleEnum.CLIENTE)
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> mine(HttpServletRequest request) {
        return ResponseEntity.ok(ordersService.mine((Integer) request.getAttribute("userId")));
    }
    @GetMapping("/sales")
    @RequiresRole(RoleEnum.VENDEDOR)
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> sales(HttpServletRequest request) {
        return ResponseEntity.ok(ordersService.sales((Integer) request.getAttribute("userId")));
    }
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> all() {
        return ResponseEntity.ok(ordersService.all());
    }
    @PatchMapping("/{id}/status")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> status(@PathVariable Integer id, @RequestBody OrderStatusRequestDTO body, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer roleId = (Integer) request.getAttribute("rolId");
        return ResponseEntity.ok(ordersService.changeStatus(id, body.getStatus(), userId, roleId));
    }
    @PatchMapping("/{id}/cancel")
    @RequiresRole(RoleEnum.CLIENTE)
    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> cancel(@PathVariable Integer id, HttpServletRequest request) {
        Integer clientId = (Integer) request.getAttribute("userId");
        return ResponseEntity.ok(ordersService.cancel(id, clientId));
    }
}