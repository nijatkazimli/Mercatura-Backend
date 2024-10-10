package com.mercatura.backend.service;

import com.mercatura.backend.dto.Responses.ProductCategoryResponse;
import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.entity.ProductCategory;
import com.mercatura.backend.repository.ProductCategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductCategoryService productCategoryService;

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
    void addProductCategory_ShouldReturnUUIDResponse() {
        ProductCategory productCategory = new ProductCategory();
        UUID id = UUID.randomUUID();
        productCategory.setId(id);

        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);

        UUIDResponse response = productCategoryService.addProductCategory(productCategory);

        assertNotNull(response);
        assertEquals(id, response.getId());
        verify(productCategoryRepository).save(productCategory);
    }

    @Test
    void getAllProductCategories_ShouldReturnListOfResponses() {
        ProductCategory productCategory1 = new ProductCategory();
        ProductCategory productCategory2 = new ProductCategory();

        when(productCategoryRepository.findAll()).thenReturn(List.of(productCategory1, productCategory2));

        List<ProductCategoryResponse> responses = productCategoryService.getAllProductCategories();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(productCategoryRepository).findAll();
    }
}
