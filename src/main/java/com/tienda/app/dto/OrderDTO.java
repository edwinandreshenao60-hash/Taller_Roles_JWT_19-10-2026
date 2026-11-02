package com.tienda.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.tienda.app.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private Integer clientId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal total;
    private OrderStatusEnum status;
    private LocalDateTime createdAt;
}