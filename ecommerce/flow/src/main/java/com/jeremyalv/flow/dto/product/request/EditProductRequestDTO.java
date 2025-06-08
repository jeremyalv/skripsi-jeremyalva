package com.jeremyalv.flow.dto.product.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditProductRequestDTO {
    // Don't add @NonNull so the PUT request doesn't include id twice (in path and in body)
    private Long id;

    private String name;

    private BigDecimal price;

    private String currency;

    private String category;
    
    private String description;
}
