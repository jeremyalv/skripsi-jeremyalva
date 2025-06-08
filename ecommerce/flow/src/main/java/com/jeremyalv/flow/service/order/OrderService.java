package com.jeremyalv.flow.service.order;

import com.jeremyalv.flow.dto.order.request.CancelOrderRequestDTO;
import com.jeremyalv.flow.dto.order.request.CreateOrderRequestDTO;
import com.jeremyalv.flow.dto.order.request.GetOrderDetailRequestDTO;
import com.jeremyalv.flow.dto.order.request.GetUserOrdersRequestDTO;
import com.jeremyalv.flow.dto.order.request.PayOrderRequestDTO;
import com.jeremyalv.flow.dto.order.response.CancelOrderResponseDTO;
import com.jeremyalv.flow.dto.order.response.CreateOrderResponseDTO;
import com.jeremyalv.flow.dto.order.response.GetOrderDetailResponseDTO;
import com.jeremyalv.flow.dto.order.response.GetUserOrdersResponseDTO;
import com.jeremyalv.flow.dto.order.response.PayOrderResponseDTO;

public interface OrderService {
    CreateOrderResponseDTO createOrder(CreateOrderRequestDTO request);
    
    GetOrderDetailResponseDTO getOrderDetail(GetOrderDetailRequestDTO request);

    GetUserOrdersResponseDTO getUserOrders(GetUserOrdersRequestDTO request);

    PayOrderResponseDTO payForOrder(PayOrderRequestDTO request);

    CancelOrderResponseDTO cancelOrder(CancelOrderRequestDTO request);
}
