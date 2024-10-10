package com.mercatura.backend.service;

import com.mercatura.backend.dto.Responses.ProductResponse;
import com.mercatura.backend.dto.Responses.ProductsWithNumPagesAndPriceRange;
import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.dto.Statistics.ProductStatistics;
import com.mercatura.backend.entity.Image;
import com.mercatura.backend.entity.Product;
import com.mercatura.backend.entity.ProductCategory;
import com.mercatura.backend.repository.CartRepository;
import com.mercatura.backend.repository.ImageRepository;
import com.mercatura.backend.repository.ProductCategoryRepository;
import com.mercatura.backend.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ProductService productService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getAllProductsStatistics_ShouldReturnStatistics() {
        Product product = new Product();
        product.setPrice(100.00);
        List<Product> products = List.of(product);
        when(productRepository.findAll()).thenReturn(products);

        ProductStatistics statistics = productService.getAllProductsStatistics();

        assertNotNull(statistics);
        verify(productRepository).findAll();
    }

    @Test
    void addProduct_ShouldReturnProduct() {
        UUID categoryId = UUID.randomUUID();
        ProductCategory category = new ProductCategory();
        Product product = new Product();

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.addProduct(product, categoryId);

        assertNotNull(result);
        assertEquals(category, result.getCategory());
        verify(productRepository).save(product);
    }

    @Test
    void addProductImage_ShouldReturnUUIDResponse() {
        UUID productId = UUID.randomUUID();
        String imageUrl = "http://example.com/image.jpg";
        Product product = new Product();
        product.setId(productId);
        product.setImages(new ArrayList<>());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(imageRepository.save(any(Image.class))).thenReturn(new Image());

        UUIDResponse response = productService.addProductImage(productId, imageUrl);

        assertNotNull(response);
        assertEquals(productId, response.getId());
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void getFilteredAndSortedProducts_ShouldReturnProducts() {
        ProductCategory category = new ProductCategory();
        category.setName("Test");
        Product product = new Product();
        product.setPrice(100.0);
        product.setCategory(category);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll((Specification<Product>) any(), any(PageRequest.class)))
                .thenReturn(productPage);

        ProductsWithNumPagesAndPriceRange result = productService.getFilteredAndSortedProducts(
                0, 10, "PRICE_ASCENDING", null, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfPages());
        verify(productRepository).findAll((Specification<Product>) any(), any(PageRequest.class));
    }

    @Test
    void getById_ShouldReturnProductResponse() {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        ProductCategory category = new ProductCategory();
        category.setName("Test Category");
        product.setCategory(category);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getById(productId);

        assertNotNull(response);
        assertEquals("Test Category", response.getCategory());
        verify(productRepository).findById(productId);
    }


    @Test
    void deleteProductById_ShouldDeleteProduct() {
        UUID productId = UUID.randomUUID();
        Product product = new Product();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findAll()).thenReturn(new ArrayList<>());

        productService.deleteProductById(productId);

        verify(productRepository).deleteById(productId);
    }

    @Test
    void deleteProductById_ShouldThrowException_WhenProductNotFound() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProductById(productId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
