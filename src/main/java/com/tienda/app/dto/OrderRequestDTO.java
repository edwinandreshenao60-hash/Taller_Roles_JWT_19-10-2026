package com.tienda.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {
    private Integer productId;
    private Integer quantity;
}