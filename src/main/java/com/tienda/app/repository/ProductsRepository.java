package com.tienda.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.app.entity.Products;

public interface ProductsRepository extends JpaRepository<Products, Integer> {
    List<Products> findByCategory_Id(Integer categoryId);
    List<Products> findBySeller_Id(Integer sellerId);
    boolean existsByCategory_Id(Integer categoryId);
}