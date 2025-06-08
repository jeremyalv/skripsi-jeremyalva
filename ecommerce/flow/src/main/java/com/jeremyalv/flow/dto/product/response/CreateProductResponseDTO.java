package com.jeremyalv.flow.dto.product.response;

import com.jeremyalv.flow.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductResponseDTO {
    private Product product;
    private boolean success;
}
