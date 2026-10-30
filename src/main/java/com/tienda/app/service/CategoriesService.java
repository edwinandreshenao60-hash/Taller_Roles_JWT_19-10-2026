package com.tienda.app.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.app.dto.*;
import com.tienda.app.entity.Categories;
import com.tienda.app.repository.CategoriesRepository;
import com.tienda.app.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final ProductsRepository productsRepository;

    public HttpGlobalResponseDTO<List<CategoryDTO>> getAll() {
        List<CategoryDTO> data = new ArrayList<>();
        for (Categories category : categoriesRepository.findAll()) data.add(toDTO(category));
        return new HttpGlobalResponseDTO<>("Categorías encontradas", data);
    }

    public HttpGlobalResponseDTO<CategoryDTO> create(CategoryRequestDTO request) {
        if (request == null) throw new AppException(HttpStatus.BAD_REQUEST, "Faltan los datos de categoría");
        if (request.getName() == null) throw new AppException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        if (request.getName().isBlank()) throw new AppException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        if (categoriesRepository.existsByName(request.getName())) throw new AppException(HttpStatus.CONFLICT, "La categoría ya existe");
        Categories category = new Categories();
        category.setName(request.getName());
        return new HttpGlobalResponseDTO<>("Categoría creada", toDTO(categoriesRepository.save(category)));
    }

    @Transactional
    public HttpGlobalResponseDTO<Void> delete(Integer id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        if (productsRepository.existsByCategory_Id(id)) throw new AppException(HttpStatus.CONFLICT, "La categoría tiene productos");
        categoriesRepository.delete(category);
        return new HttpGlobalResponseDTO<>("Categoría eliminada", null);
    }

    private CategoryDTO toDTO(Categories category) { return new CategoryDTO(category.getId(), category.getName()); }
}