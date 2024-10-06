package com.mercatura.backend.service;

import com.mercatura.backend.dto.ProductCategoryResponse;
import com.mercatura.backend.dto.UUIDResponse;
import com.mercatura.backend.entity.ProductCategory;
import com.mercatura.backend.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public UUIDResponse addProductCategory(ProductCategory productCategory) {
        ProductCategory newProductCategory = productCategoryRepository.save(productCategory);
        return new UUIDResponse(newProductCategory.getId());
    }

    public List<ProductCategoryResponse> getAllProductCategories() {
        return productCategoryRepository.findAll().stream()
                .map(ProductCategoryResponse::new)
                .collect(Collectors.toList());
    }
}
