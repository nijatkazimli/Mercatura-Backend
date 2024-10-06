package com.mercatura.backend.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsWithNumPagesAndPriceRange {
    private Integer numberOfPages;
    private MinMaxResponse priceRange;
    private List<ProductResponse> products;
}
