package com.tienda.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.app.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}