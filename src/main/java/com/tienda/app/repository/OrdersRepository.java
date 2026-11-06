package com.tienda.app.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.app.entity.Orders;
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByClient_Id(Integer clientId);
    List<Orders> findByProduct_Seller_Id(Integer sellerId);
    boolean existsByProduct_Id(Integer productId);
}