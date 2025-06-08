package com.jeremyalv.flow.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @NotBlank
    private String id;

    @NotBlank
    private String userId;

    @NotNull
    private Map<String, Integer> cart;
}
