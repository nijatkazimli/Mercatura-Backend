package com.mercatura.backend.dto.Statistics;

import com.mercatura.backend.entity.Cart;
import com.mercatura.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartStatistics {
    private Integer totalCount;
    private Double totalPrice;
    private Integer paidCount;
    private Double paidPrice;
    private Integer unPaidCount;
    private Double unPaidPrice;

    public CartStatistics(List<Cart> carts) {
        this.totalCount = carts.size();

        this.paidPrice = carts.stream()
                .filter(Cart::isPaid)
                .flatMap(cart -> cart.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();

        this.unPaidPrice = carts.stream()
                .filter(cart -> !cart.isPaid())
                .flatMap(cart -> cart.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();

        this.paidCount = (int) carts.stream().filter(Cart::isPaid).count();
        this.unPaidCount = totalCount - paidCount;
        this.totalPrice = paidPrice + unPaidPrice;
    }
}
