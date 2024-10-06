package com.mercatura.backend.service;

import com.mercatura.backend.dto.CartResponse;
import com.mercatura.backend.dto.Statistics.CartStatistics;
import com.mercatura.backend.dto.UUIDResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Cart;
import com.mercatura.backend.entity.Product;
import com.mercatura.backend.repository.CartRepository;
import com.mercatura.backend.repository.ProductRepository;
import com.mercatura.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public CartStatistics getAllCartsStatistics() {
        List<Cart> carts = cartRepository.findAll();
        return new CartStatistics(carts);
    }

    public List<CartResponse> getAllCartsByUserId(UUID userId) {
        boolean exists = userRepository.existsById(userId);
        if (exists) {
            return cartRepository.findByUserId(userId).stream().map(CartResponse::new).collect(Collectors.toList());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id %s not found", userId));
        }
    }

    public UUIDResponse createCart(UUID userId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setPaid(false);
        newCart.setProducts(new HashSet<>());
        Cart addedCart = cartRepository.save(newCart);

        return new UUIDResponse(addedCart.getId());
    }

    public void addToCartByProductId(UUID cartId, UUID productId) {
        Optional<Product> product = productRepository.findById(productId);
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (product.isPresent() && cart.isPresent()) {
            if (cart.get().getProducts().add(product.get())) {
                cartRepository.save(cart.get());
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("Product with id %s already exists", productId));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Product with id %s or cart with id %s not found", productId, cartId));
        }
    }

    public void removeFromCartByProductId(UUID cartId, UUID productId) {
        Optional<Product> product = productRepository.findById(productId);
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (product.isPresent() && cart.isPresent()) {
            if (cart.get().getProducts().remove(product.get())) {
                cartRepository.save(cart.get());
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("Product with id %s does not exist in the cart", productId));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Product with id %s or cart with id %s not found", productId, cartId));
        }
    }

    public void payTheCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Cart not found"));
        cart.setPaid(true);
        cartRepository.save(cart);
    }

    public void deleteCartById(UUID id) {
        boolean exists = cartRepository.existsById(id);
        if (exists) {
            cartRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cart with id %s not found", id));
        }
    }
}
