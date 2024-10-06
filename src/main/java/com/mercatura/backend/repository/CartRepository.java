package com.mercatura.backend.repository;

import com.mercatura.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    List<Cart> findByUserId(UUID userId);
}
