package com.mercatura.backend.service;

import com.mercatura.backend.dto.Responses.CartResponse;
import com.mercatura.backend.dto.Statistics.CartStatistics;
import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Cart;
import com.mercatura.backend.entity.Product;
import com.mercatura.backend.repository.CartRepository;
import com.mercatura.backend.repository.ProductRepository;
import com.mercatura.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

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
    void getAllCartsStatistics_ShouldReturnStatistics() {
        List<Cart> carts = new ArrayList<>();
        when(cartRepository.findAll()).thenReturn(carts);

        CartStatistics statistics = cartService.getAllCartsStatistics();

        assertNotNull(statistics);
        verify(cartRepository).findAll();
    }

    @Test
    void getAllCartsByUserId_ShouldReturnCarts() {
        UUID userId = UUID.randomUUID();
        ApplicationUser user = new ApplicationUser();
        user.setId(userId);

        Cart cart = new Cart();
        cart.setUser(user);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartRepository.findByUserId(userId)).thenReturn(List.of(cart));

        List<CartResponse> responses = cartService.getAllCartsByUserId(userId);

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        verify(cartRepository).findByUserId(userId);
    }

    @Test
    void createCart_ShouldReturnUUIDResponse() {
        UUID userId = UUID.randomUUID();
        ApplicationUser user = new ApplicationUser();
        user.setId(userId);
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        UUIDResponse response = cartService.createCart(userId);

        assertNotNull(response);
        assertEquals(cart.getId(), response.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addToCartByProductId_ShouldAddProduct() {
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        Cart cart = new Cart();
        cart.setProducts(new HashSet<>());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.addToCartByProductId(cartId, productId);

        assertTrue(cart.getProducts().contains(product));
        verify(cartRepository).save(cart);
    }

    @Test
    void removeFromCartByProductId_ShouldRemoveProduct() {
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        Cart cart = new Cart();
        cart.setProducts(new HashSet<>(Set.of(product)));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.removeFromCartByProductId(cartId, productId);

        assertFalse(cart.getProducts().contains(product));
        verify(cartRepository).save(cart);
    }

    @Test
    void payTheCart_ShouldSetCartAsPaid() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.payTheCart(cartId);

        assertTrue(cart.isPaid());
        verify(cartRepository).save(cart);
    }

    @Test
    void deleteCartById_ShouldDeleteCart() {
        UUID cartId = UUID.randomUUID();

        when(cartRepository.existsById(cartId)).thenReturn(true);

        cartService.deleteCartById(cartId);

        verify(cartRepository).deleteById(cartId);
    }

    @Test
    void getAllCartsByUserId_ShouldThrowException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            cartService.getAllCartsByUserId(userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void addToCartByProductId_ShouldThrowException_WhenProductOrCartNotFound() {
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            cartService.addToCartByProductId(cartId, productId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void removeFromCartByProductId_ShouldThrowException_WhenProductNotInCart() {
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        Cart cart = new Cart();
        cart.setProducts(new HashSet<>());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            cartService.removeFromCartByProductId(cartId, productId);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }
}
