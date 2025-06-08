package com.jeremyalv.flow.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @NotNull
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private String orderId;

    @NotNull
    private Instant created;
}
