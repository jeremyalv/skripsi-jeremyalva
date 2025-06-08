package com.jeremyalv.flow.dto.order.response;

import java.util.List;

import com.jeremyalv.flow.model.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class GetUserOrdersResponseDTO {
    private List<Order> orders;
    private boolean success;
}
