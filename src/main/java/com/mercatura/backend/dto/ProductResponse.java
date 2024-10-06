package com.mercatura.backend.dto;

import com.mercatura.backend.entity.Image;
import com.mercatura.backend.entity.Product;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private Double rating;
    private List<String> images;
    private String category;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.rating = product.getRating();
        this.images = product.getImages().stream().map(Image::getImageUrl).toList();
        this.category = product.getCategory().getName();
    }
}
