package com.mercatura.backend.controller;

import com.mercatura.backend.dto.Responses.CartResponse;
import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
@Validated
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Tag(name = "Cart")
    @Operation(summary = "Gets the carts of the user with the given ID.")
    @GetMapping()
    public List<CartResponse> getAllCartsByUserId(@NotNull @RequestParam UUID userId) {
        return cartService.getAllCartsByUserId(userId);
    }

    @Tag(name = "Cart")
    @Operation(summary = "Creates a new cart.",
            description = "Creates a new cart and provides its UUID.")
    @PostMapping()
    public UUIDResponse createCart(
            @Parameter(description = "ID of the user for which the cart is created.")
            @RequestParam UUID userId
    ) {
        return cartService.createCart(userId);
    }

    @Tag(name = "Cart")
    @Operation(summary = "Adds the product to the cart.",
            description = "Adds the product with a given ID to the cart.")
    @PatchMapping("/{cartId}/add")
    public void addToCartByProductId(
            @Parameter(description = "ID of the cart to which the product is added.")
            @PathVariable UUID cartId,
            @Parameter(description = "ID of the product which is added to the cart.")
            @RequestParam UUID productId
    ) {
        cartService.addToCartByProductId(cartId, productId);
    }

    @Tag(name = "Cart")
    @Operation(summary = "Removes the product from the cart.",
            description = "Removes the product with a given ID from the cart.")
    @PatchMapping("/{cartId}/remove")
    public void removeFromCartByProductId(
            @Parameter(description = "ID of the cart from which the product is removed.")
            @PathVariable UUID cartId,
            @Parameter(description = "ID of the product which is removed from the cart.")
            @RequestParam UUID productId
    ) {
        cartService.removeFromCartByProductId(cartId, productId);
    }

    @Tag(name = "Cart")
    @Operation(summary = "Pays the cart.")
    @PatchMapping("/{cartId}/pay")
    public void payTheCart(
            @Parameter(description = "ID of the cart which is about to get paid.")
            @PathVariable UUID cartId
    ) {
        cartService.payTheCart(cartId);
    }


    @Tag(name = "Cart")
    @Operation(summary = "Deletes the cart with a given ID.")
    @DeleteMapping("/{cartId}")
    public void deleteCartById(
            @Parameter(description = "ID of the cart which is being deleted.")
            @PathVariable UUID cartId
    ) {
        cartService.deleteCartById(cartId);
    }
}
