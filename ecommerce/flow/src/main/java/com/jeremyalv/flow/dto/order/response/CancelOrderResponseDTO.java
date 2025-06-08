package com.jeremyalv.flow.dto.order.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class CancelOrderResponseDTO {
    private Long id;
    private boolean success;
}
