package com.mercatura.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id")
    private UUID id;

    private String content;

    @NotNull
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser author;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
