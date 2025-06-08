package com.jeremyalv.flow.dto.product.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDTO {
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be positive or zero")
    private BigDecimal price;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must follow ISO 4217")
    private String currency;

    private String category;

    private String description;
}
