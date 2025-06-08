package com.jeremyalv.flow.dto.order.response;

import com.jeremyalv.flow.model.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class GetOrderDetailResponseDTO {
    private Order order;
    private boolean success;
}
