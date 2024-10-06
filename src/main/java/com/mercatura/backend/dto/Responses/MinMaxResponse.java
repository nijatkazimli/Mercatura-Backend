package com.mercatura.backend.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinMaxResponse {
    private Double min;
    private Double max;
}
