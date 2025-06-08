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
public class PaymentStatus {
    @NotNull
    private String id;

    @NotNull
    private String paymentId;

    @NotNull
    private int status;

    @NotNull
    private Instant created;
}
