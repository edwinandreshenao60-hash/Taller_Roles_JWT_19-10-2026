package com.tienda.app.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.app.dto.*;
import com.tienda.app.entity.Categories;
import com.tienda.app.entity.Products;
import com.tienda.app.entity.Users;
import com.tienda.app.enums.RoleEnum;
import com.tienda.app.repository.CategoriesRepository;
import com.tienda.app.repository.OrdersRepository;
import com.tienda.app.repository.ProductsRepository;
import com.tienda.app.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final CategoriesRepository categoriesRepository;
    private final UsersRepository usersRepository;
    private final OrdersRepository ordersRepository;

    public HttpGlobalResponseDTO<List<ProductDTO>> catalog(Integer categoryId) {
        List<Products> products;
        if (categoryId == null) products = productsRepository.findAll();
        else products = productsRepository.findByCategory_Id(categoryId);
        List<ProductDTO> data = new ArrayList<>();
        for (Products product : products) data.add(toDTO(product));
        return new HttpGlobalResponseDTO<>("Productos encontrados", data);
    }

    public HttpGlobalResponseDTO<ProductDTO> get(Integer id) {
        return new HttpGlobalResponseDTO<>("Producto encontrado", toDTO(findProduct(id)));
    }

    public HttpGlobalResponseDTO<List<ProductDTO>> mine(Integer sellerId) {
        List<ProductDTO> data = new ArrayList<>();
        for (Products product : productsRepository.findBySeller_Id(sellerId)) data.add(toDTO(product));
        return new HttpGlobalResponseDTO<>("Productos del vendedor", data);
    }

    public HttpGlobalResponseDTO<ProductDTO> create(ProductRequestDTO request, Integer sellerId) {
        validate(request);
        Users seller = usersRepository.findById(sellerId)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Vendedor no encontrado"));
        Products product = new Products();
        updateFields(product, request);
        product.setSeller(seller);
        return new HttpGlobalResponseDTO<>("Producto creado", toDTO(productsRepository.save(product)));
    }

    @Transactional
    public HttpGlobalResponseDTO<ProductDTO> update(Integer id, ProductRequestDTO request, Integer userId, Integer roleId) {
        validate(request);
        Products product = findProduct(id);
        checkOwner(product, userId, roleId);
        updateFields(product, request);
        return new HttpGlobalResponseDTO<>("Producto actualizado", toDTO(product));
    }

    @Transactional
    public HttpGlobalResponseDTO<Void> delete(Integer id, Integer userId, Integer roleId) {
        Products product = findProduct(id);
        checkOwner(product, userId, roleId);
        if (ordersRepository.existsByProduct_Id(id)) throw new AppException(HttpStatus.CONFLICT, "El producto tiene pedidos relacionados");
        productsRepository.delete(product);
        return new HttpGlobalResponseDTO<>("Producto eliminado", null);
    }

    private void updateFields(Products product, ProductRequestDTO request) {
        Categories category = categoriesRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
    }

    private void validate(ProductRequestDTO request) {
        if (request == null) throw new AppException(HttpStatus.BAD_REQUEST, "Faltan los datos del producto");
        if (request.getName() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        if (request.getName().isBlank()) throw new AppException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        if (request.getPrice() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El precio es obligatorio");
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) throw new AppException(HttpStatus.BAD_REQUEST, "El precio debe ser positivo");
        if (request.getStock() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El stock es obligatorio");
        if (request.getStock() < 0) throw new AppException(HttpStatus.BAD_REQUEST, "El stock no puede ser negativo");
        if (request.getCategoryId() == null) throw new AppException(HttpStatus.BAD_REQUEST, "La categoría es obligatoria");
    }

    private void checkOwner(Products product, Integer userId, Integer roleId) {
        if (roleId.equals(RoleEnum.ADMIN.getId())) return;
        if (product.getSeller().getId().equals(userId)) return;
        throw new AppException(HttpStatus.FORBIDDEN, "No puedes modificar productos de otro vendedor");
    }

    private Products findProduct(Integer id) {
        return productsRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public ProductDTO toDTO(Products product) {
        return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                product.getStock(), product.getCategory().getId(), product.getSeller().getId());
    }
}