package com.mercatura.backend.controller;

import com.mercatura.backend.dto.Responses.ProductCategoryResponse;
import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.entity.ProductCategory;
import com.mercatura.backend.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @Tag(name = "Product Category")
    @Operation(summary = "Gets all available product categories.")
    @GetMapping()
    public List<ProductCategoryResponse> getAllProductCategories() {
        return productCategoryService.getAllProductCategories();
    }

    @Tag(name = "Product Category")
    @Operation(summary = "Adds new product category.",
        description = "Adds a new product category and provides its UUID.")
    @PostMapping()
    public UUIDResponse addProductCategory(@Valid @RequestBody ProductCategory productCategory) {
        return productCategoryService.addProductCategory(productCategory);
    }
}
