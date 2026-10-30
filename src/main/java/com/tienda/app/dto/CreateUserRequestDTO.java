package com.tienda.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDTO {
    private String username;
    private String email;
    private String password;
    private Integer rolId;
}