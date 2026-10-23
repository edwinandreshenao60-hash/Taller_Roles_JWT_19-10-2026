package com.tienda.app.enums;

public enum RoleEnum {
    ADMIN(1), CLIENTE(2), VENDEDOR(3);

    private final Integer id;

    RoleEnum(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}