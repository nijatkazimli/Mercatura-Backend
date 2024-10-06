package com.mercatura.backend.controller;

import com.mercatura.backend.dto.Responses.ProductResponse;
import com.mercatura.backend.dto.Responses.ProductsWithNumPagesAndPriceRange;
import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.entity.Product;
import com.mercatura.backend.service.ImageService;
import com.mercatura.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


// TODO: Add PUT/POST endpoints. (after adding merchandiser role/logic to the app)
// TODO: Add image fields to User, Product. (after figuring out image storage options and handling)
// TODO: Add password validation and password reset.
// TODO: Refactor filter chain. (after merchandiser).
// TODO: Add Chat entity, dto, repository, service, controller. (least priority).
// TODO: Add unit tests.
// TODO: Dockerize the application.
// TODO: Add CI/CD (GitHub Actions).
// TODO: Deploy (Terraform IaC).
@RestController
@RequestMapping("/product")
@Validated
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;

    public ProductController(ProductService productService, ImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
    }

    @Tag(name = "Product")
    @Operation(summary = "Gets all available products with sorting, filtering, and pagination" +
            " together with the total number of pages.")
    @GetMapping()
    public ProductsWithNumPagesAndPriceRange getAllProducts(
            @Parameter(description = "Page number for pagination. Default 0.")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size for pagination. Default 15.")
            @RequestParam(defaultValue = "15") Integer size,
            @Parameter(description = "Sort by price or rating (asc/desc).")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Minimum price filter.")
            @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price filter.")
            @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Minimum rating filter.")
            @RequestParam(required = false) Double minRating,
            @Parameter(description = "Maximum rating filter.")
            @RequestParam(required = false) Double maxRating,
            @Parameter(description = "Category filter.")
            @RequestParam(required = false) String category,
            @Parameter(description = "Name filter.")
            @RequestParam(required = false) String name
    ) {
        return productService.getFilteredAndSortedProducts(
                page,
                size,
                sortBy,
                minPrice,
                maxPrice,
                minRating,
                maxRating,
                category,
                name
        );
    }

    @Tag(name = "Product")
    @Operation(summary = "Gets the product with the given ID")
    @GetMapping("/{id}")
    public ProductResponse getProductById(@Parameter(
                                            description = "ID of the product to be retrieved."
                                            ) @PathVariable UUID id) {
        return productService.getById(id);
    }

    @Tag(name = "Product")
    @Operation(summary = "Adds a new product.",
        description = "Adds a new product and provides its UUID.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANDISER')")
    @PostMapping()
    public UUIDResponse addProduct(@Valid @RequestBody Product product,
                                   @Parameter(
                                           description = "ID of the product category this product needs to be added."
                                   ) @RequestParam UUID categoryId) {
        return new UUIDResponse(productService.addProduct(product, categoryId).getId());
    }

    @Tag(name = "Product")
    @Operation(summary = "Adds a new image to the product with the given ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANDISER')")
    @PostMapping("/{id}/image")
    public UUIDResponse addProductImage(
            @Parameter(description = "ID of the product.")
            @PathVariable UUID id,
            @Parameter(description = "The image file.")
            @RequestParam MultipartFile file) throws IOException {
        String imageUrl = imageService.saveImage(file);
        return productService.addProductImage(id, imageUrl);
    }

    @Tag(name = "Product")
    @Operation(summary = "Deletes the product.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANDISER')")
    @DeleteMapping("/{id}")
    public void deleteProductById(@Parameter(
                                        description = "ID of the product to be deleted."
                                    ) @PathVariable UUID id) {
        productService.deleteProductById(id);
    }
}
