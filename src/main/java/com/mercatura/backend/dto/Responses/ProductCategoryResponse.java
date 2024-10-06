package com.mercatura.backend.dto.Responses;

import com.mercatura.backend.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {
    private UUID id;
    private String name;

    public ProductCategoryResponse(ProductCategory productCategory) {
        this.id = productCategory.getId();
        this.name = productCategory.getName();
    }
}
