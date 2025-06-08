package com.jeremyalv.flow.dto.order.request;

import java.util.List;

import com.jeremyalv.flow.dto.order.common.OrderItemDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class CreateOrderRequestDTO {
    private Long userId;

    private List<OrderItemDTO> orderItems;
}
