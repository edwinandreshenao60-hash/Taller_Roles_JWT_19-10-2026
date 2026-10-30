package com.tienda.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.app.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    boolean existsByName(String name);
}