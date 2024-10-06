package com.mercatura.backend.service;

import com.mercatura.backend.dto.MinMaxResponse;
import com.mercatura.backend.dto.ProductResponse;
import com.mercatura.backend.dto.ProductsWithNumPagesAndPriceRange;
import com.mercatura.backend.dto.Statistics.ProductStatistics;
import com.mercatura.backend.dto.UUIDResponse;
import com.mercatura.backend.entity.Image;
import com.mercatura.backend.entity.Product;
import com.mercatura.backend.entity.ProductCategory;
import com.mercatura.backend.repository.CartRepository;
import com.mercatura.backend.repository.ImageRepository;
import com.mercatura.backend.repository.ProductCategoryRepository;
import com.mercatura.backend.repository.ProductRepository;
import com.mercatura.backend.specification.ProductSpecifications;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CartRepository cartRepository;
    private final ImageRepository imageRepository;

    public ProductService(
            ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository,
            CartRepository cartRepository,
            ImageRepository imageRepository
    ) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.cartRepository = cartRepository;
        this.imageRepository = imageRepository;
    }

    public ProductStatistics getAllProductsStatistics() {
        List<Product> products = productRepository.findAll();
        return new ProductStatistics(products);
    }

    public Product addProduct(Product product, UUID productCategoryId) {
        ProductCategory category = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        product.setCategory(category);
        product.setReviews(new ArrayList<>());
        return productRepository.save(product);
    }

    public UUIDResponse addProductImage(UUID id, String imageUrl) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Image image = new Image();
        image.setImageUrl(imageUrl);
        image.setProduct(product);
        imageRepository.save(image);

        product.getImages().add(image);
        productRepository.save(product);
        return new UUIDResponse(product.getId());
    }

    public ProductsWithNumPagesAndPriceRange getFilteredAndSortedProducts(
            Integer page,
            Integer size,
            String sortBy,
            Double minPrice,
            Double maxPrice,
            Double minRating,
            Double maxRating,
            String category,
            String name
    ) {
        Pageable pageable = PageRequest.of(page, size, getSortOrder(sortBy));

        Specification<Product> spec = Specification.where(null);
        Specification<Product> categoryAndNameSpec = Specification.where(null);

        if (minPrice != null) {
            spec = spec.and(ProductSpecifications.hasPriceGreaterThanOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ProductSpecifications.hasPriceLessThanOrEqual(maxPrice));
        }
        if (minRating != null) {
            spec = spec.and(ProductSpecifications.hasRatingGreaterThanOrEqual(minRating));
        }
        if (maxRating != null) {
            spec = spec.and(ProductSpecifications.hasRatingLessThanOrEqual(maxRating));
        }
        if (category != null) {
            spec = spec.and(ProductSpecifications.hasCategory(category));
            categoryAndNameSpec = categoryAndNameSpec.and(ProductSpecifications.hasCategory(category));
        }
        if (name != null) {
            spec = spec.and(ProductSpecifications.includesName(name));
            categoryAndNameSpec = categoryAndNameSpec.and(ProductSpecifications.includesName(name));
        }

        Page<Product> products = productRepository.findAll(spec, pageable);
        List<Product> productByCategoryAndName = productRepository.findAll(categoryAndNameSpec);

        Integer numOfPages = products.getTotalPages();
        List<ProductResponse> response = products.stream().map(ProductResponse::new).toList();
        Double min = productByCategoryAndName.stream().mapToDouble(Product::getPrice).min().orElse(0.0);
        Double max = productByCategoryAndName.stream().mapToDouble(Product::getPrice).max().orElse(999.99);
        MinMaxResponse priceRange = new MinMaxResponse(min, max);
        return new ProductsWithNumPagesAndPriceRange(numOfPages, priceRange, response);
    }

    private Sort getSortOrder(String sortBy) {
        if ("PRICE_ASCENDING".equals(sortBy)) {
            return Sort.by("price").ascending();
        } else if ("PRICE_DESCENDING".equals(sortBy)) {
            return Sort.by("price").descending();
        } else if ("RATING_DESCENDING".equals(sortBy)) {
            return Sort.by("rating").descending();
        } else if ("RATING_ASCENDING".equals(sortBy)) {
            return Sort.by("rating").ascending();
        } else if ("ALPHABETICALLY".equals(sortBy)) {
            return Sort.by("name").ascending();
        }
        return Sort.by("price").descending();
    }

    public ProductResponse getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return new ProductResponse(product);
    }

    public void deleteProductById(UUID id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            cartRepository.findAll().forEach(cart -> {
                if (cart.getProducts().remove(product.get())) {
                    cartRepository.save(cart);
                }
            });
            productRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Product with id %s not found", id));
        }
    }
}
