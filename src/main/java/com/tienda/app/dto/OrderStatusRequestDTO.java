package com.tienda.app.dto;

import com.tienda.app.enums.OrderStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequestDTO {
    private OrderStatusEnum status;
}