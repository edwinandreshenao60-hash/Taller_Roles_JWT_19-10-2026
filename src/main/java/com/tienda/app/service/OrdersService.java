package com.tienda.app.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.app.dto.*;
import com.tienda.app.entity.Orders;
import com.tienda.app.entity.Products;
import com.tienda.app.entity.Users;
import com.tienda.app.enums.OrderStatusEnum;
import com.tienda.app.enums.RoleEnum;
import com.tienda.app.repository.OrdersRepository;
import com.tienda.app.repository.ProductsRepository;
import com.tienda.app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public HttpGlobalResponseDTO<OrderDTO> create(OrderRequestDTO request, Integer clientId) {
        if (request == null) throw new AppException(HttpStatus.BAD_REQUEST, "Faltan los datos del pedido");
        if (request.getProductId() == null) throw new AppException(HttpStatus.BAD_REQUEST, "productId es obligatorio");
        if (request.getQuantity() == null) throw new AppException(HttpStatus.BAD_REQUEST, "quantity es obligatorio");
        if (request.getQuantity() <= 0) throw new AppException(HttpStatus.BAD_REQUEST, "quantity debe ser positivo");
        Products product = productsRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        if (product.getStock() < request.getQuantity()) throw new AppException(HttpStatus.BAD_REQUEST, "Stock insuficiente");
        Users client = usersRepository.findById(clientId)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Cliente no encontrado"));
        product.setStock(product.getStock() - request.getQuantity());
        Orders order = new Orders();
        order.setClient(client);
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotal(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus(OrderStatusEnum.PENDIENTE);
        order.setCreatedAt(LocalDateTime.now());
        productsRepository.save(product);
        return new HttpGlobalResponseDTO<>("Pedido creado", toDTO(ordersRepository.save(order)));
    }

    public HttpGlobalResponseDTO<List<OrderDTO>> mine(Integer clientId) {
        return new HttpGlobalResponseDTO<>("Pedidos del cliente", toDTOList(ordersRepository.findByClient_Id(clientId)));
    }

    public HttpGlobalResponseDTO<List<OrderDTO>> sales(Integer sellerId) {
        return new HttpGlobalResponseDTO<>("Ventas del vendedor", toDTOList(ordersRepository.findByProduct_Seller_Id(sellerId)));
    }

    public HttpGlobalResponseDTO<List<OrderDTO>> all() {
        return new HttpGlobalResponseDTO<>("Pedidos encontrados", toDTOList(ordersRepository.findAll()));
    }

    @Transactional
    public HttpGlobalResponseDTO<OrderDTO> changeStatus(Integer id, OrderStatusEnum status, Integer userId, Integer roleId) {
        if (status == null) throw new AppException(HttpStatus.BAD_REQUEST, "El status es obligatorio");
        Orders order = findOrder(id);
        if (roleId.equals(RoleEnum.VENDEDOR.getId())) {
            if (!order.getProduct().getSeller().getId().equals(userId)) {
                throw new AppException(HttpStatus.FORBIDDEN, "El pedido no pertenece a tus productos");
            }
        }
        order.setStatus(status);
        return new HttpGlobalResponseDTO<>("Estado del pedido actualizado", toDTO(order));
    }

    @Transactional
    public HttpGlobalResponseDTO<OrderDTO> cancel(Integer id, Integer clientId) {
        Orders order = findOrder(id);
        if (!order.getClient().getId().equals(clientId)) throw new AppException(HttpStatus.FORBIDDEN, "El pedido no te pertenece");
        if (!order.getStatus().equals(OrderStatusEnum.PENDIENTE)) {
            throw new AppException(HttpStatus.CONFLICT, "Solo se pueden cancelar pedidos pendientes");
        }
        Products product = order.getProduct();
        product.setStock(product.getStock() + order.getQuantity());
        order.setStatus(OrderStatusEnum.CANCELADO);
        productsRepository.save(product);
        return new HttpGlobalResponseDTO<>("Pedido cancelado y stock devuelto", toDTO(order));
    }

    private Orders findOrder(Integer id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    private List<OrderDTO> toDTOList(List<Orders> orders) {
        List<OrderDTO> data = new ArrayList<>();
        for (Orders order : orders) data.add(toDTO(order));
        return data;
    }

    private OrderDTO toDTO(Orders order) {
        return new OrderDTO(order.getId(), order.getClient().getId(), order.getProduct().getId(),
                order.getProduct().getName(), order.getQuantity(), order.getTotal(), order.getStatus(), order.getCreatedAt());
    }
}