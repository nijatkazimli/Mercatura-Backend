package com.mercatura.backend.repository;

import com.mercatura.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, PagingAndSortingRepository<Review, UUID> {
    Page<Review> findByProductId(UUID id, Pageable pageable);
}
