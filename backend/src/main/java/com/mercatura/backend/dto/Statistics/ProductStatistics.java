package com.mercatura.backend.dto.Statistics;

import com.mercatura.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatistics {
    private Integer totalCount;
    private Double totalPrice;

    public ProductStatistics(List<Product> products) {
        this.totalCount = products.size();
        this.totalPrice = products.stream().mapToDouble(Product::getPrice).sum();
    }
}
