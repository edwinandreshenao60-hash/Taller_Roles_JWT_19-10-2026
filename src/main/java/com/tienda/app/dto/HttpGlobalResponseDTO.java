package com.tienda.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpGlobalResponseDTO<T> {
    private String message;
    private T data;
}