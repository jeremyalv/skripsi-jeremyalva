package com.jeremyalv.flow.dto.product.response;

import java.util.List;

import com.jeremyalv.flow.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductsResponseDTO {
    List<Product> products;
    private boolean success;
}
