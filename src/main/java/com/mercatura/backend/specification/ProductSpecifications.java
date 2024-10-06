package com.mercatura.backend.specification;

import org.springframework.data.jpa.domain.Specification;
import com.mercatura.backend.entity.Product;

public class ProductSpecifications {
    public static Specification<Product> hasPriceGreaterThanOrEqual(Double minPrice) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> hasPriceLessThanOrEqual(Double maxPrice) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> hasRatingGreaterThanOrEqual(Double minRating) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<Product> hasRatingLessThanOrEqual(Double maxRating) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("rating"), maxRating);
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, builder) -> builder.equal(root.get("category").get("name"), category);
    }

    public static Specification<Product> includesName(String productName) {
        return (root, query, builder) -> builder.like(root.get("name"), "%" + productName + "%");
    }
}
