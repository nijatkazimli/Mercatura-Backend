package com.mercatura.backend.repository;

import com.mercatura.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<ApplicationUser, UUID>, PagingAndSortingRepository<ApplicationUser, UUID> {
    Optional<ApplicationUser> findByUsername(String username);
}
