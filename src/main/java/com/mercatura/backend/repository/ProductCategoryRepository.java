package com.mercatura.backend.repository;

import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    Optional<ProductCategory> findByName(String name);
}
