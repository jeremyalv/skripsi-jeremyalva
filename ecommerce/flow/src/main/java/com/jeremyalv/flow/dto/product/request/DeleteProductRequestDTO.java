package com.jeremyalv.flow.dto.product.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductRequestDTO {
    @NotNull(message = "Product Id cannot be empty")
    private Long id;
}
