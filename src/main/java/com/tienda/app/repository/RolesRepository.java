package com.tienda.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.app.entity.Roles;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByName(String name);
}