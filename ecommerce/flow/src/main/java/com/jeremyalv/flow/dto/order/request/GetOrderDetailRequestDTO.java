package com.jeremyalv.flow.dto.order.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class GetOrderDetailRequestDTO {
    private Long orderId;
    private Long userId;
}
