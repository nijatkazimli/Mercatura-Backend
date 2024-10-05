package com.mercatura.backend.dto;

import com.mercatura.backend.entity.Cart;
import com.mercatura.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private UUID id;
    private UUID userId;
    private boolean isPaid;
    private Double totalValue;
    private Set<UUID> productIds;

    public CartResponse(Cart cart) {
        this.id = cart.getId();
        this.userId = cart.getUser().getId();
        this.isPaid = cart.isPaid();
        this.totalValue = cart.getProducts().stream().mapToDouble(Product::getPrice).sum();
        this.productIds = cart.getProducts().stream().map(Product::getId).collect(Collectors.toSet());
    }
}
