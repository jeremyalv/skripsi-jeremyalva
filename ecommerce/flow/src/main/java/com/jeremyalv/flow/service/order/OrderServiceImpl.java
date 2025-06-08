package com.jeremyalv.flow.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeremyalv.flow.constants.Constants;
import com.jeremyalv.flow.dto.analytics.MessageEnvelopeDTO;
import com.jeremyalv.flow.dto.order.common.OrderItemDTO;
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
import com.jeremyalv.flow.exceptions.order.OrderAlreadyPaidException;
import com.jeremyalv.flow.exceptions.order.OrderNotFoundException;
import com.jeremyalv.flow.exceptions.order.OrderStatusTerminalException;
import com.jeremyalv.flow.exceptions.product.ProductNotFoundException;
import com.jeremyalv.flow.model.Order;
import com.jeremyalv.flow.model.OrderItem;
import com.jeremyalv.flow.model.Product;
import com.jeremyalv.flow.repository.OrderRepository;
import com.jeremyalv.flow.repository.ProductRepository;
import com.jeremyalv.flow.strategy.MessagingStrategy;
import com.jeremyalv.flow.utils.Utils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final MessagingStrategy<String, Order> orderMessagingStrategy;

    private final MeterRegistry meterRegistry;

    @Override
    @Transactional
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        log.info("Creating order for user ID: %s", request.getUserId());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        String currency = null;

        for (OrderItemDTO itemDTO : request.getOrderItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemDTO.getProductId()));
            
            BigDecimal pricePerUnit = product.getPrice();
            OrderItem item = OrderItem.builder()
                    .productId(itemDTO.getProductId())
                    .quantity(itemDTO.getQuantity())
                    .pricePerUnit(pricePerUnit)
                    .currency(product.getCurrency())
                    .build();

            items.add(item);

            BigDecimal orderItemSubtotal = pricePerUnit.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            
            totalAmount = totalAmount.add(orderItemSubtotal);
            currency = product.getCurrency();
        }

        Order order = Order.builder()
                .userId(request.getUserId())
                .totalAmount(totalAmount)
                .currency(currency != null ? currency : "USD")
                .currentStatus(Constants.ORDER_STATUS_PLACED)
                .build();

        order.setOrderItems(items);
        for (OrderItem item : items) {
            item.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: %s", savedOrder.getId());

        publishOrderEvent(Constants.PUBLISH_ORDER_EVENT_NAME, request.getUserId(), savedOrder);

        return CreateOrderResponseDTO.builder()
                .order(savedOrder)
                .success(true)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public GetOrderDetailResponseDTO getOrderDetail(GetOrderDetailRequestDTO request) {
        log.info("Fetching order details for order id %s", request.getOrderId());
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));

        log.info("Order %s found", order.getId());

        return GetOrderDetailResponseDTO.builder()
                .order(order)
                .success(true)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public GetUserOrdersResponseDTO getUserOrders(GetUserOrdersRequestDTO request) {
        log.info("Fetching orders for user id %s", request.getUserId());

        List<Order> orders = orderRepository.findByUserId(request.getUserId());

        log.info("Found %d orders for user id %s", orders.size(), request.getUserId());

        return GetUserOrdersResponseDTO.builder()
                .orders(orders)
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public PayOrderResponseDTO payForOrder(PayOrderRequestDTO request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));
        
        if (isOrderStatusTerminal(order)) {
            throw new OrderStatusTerminalException(order.getId());
        }
        
        if (isOrderStatusPaid(order)) {
            throw new OrderAlreadyPaidException(order.getId());
        }

        order.setCurrentStatus(Constants.ORDER_STATUS_PAID);

        publishOrderEvent(Constants.PUBLISH_PAY_ORDER_EVENT_NAME, request.getUserId(), order);

        return PayOrderResponseDTO.builder()
                .order(order)
                .success(true)
                .build();
    };

    @Override
    @Transactional
    public CancelOrderResponseDTO cancelOrder(CancelOrderRequestDTO request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));
        
        if (isOrderStatusTerminal(order)) {
            throw new OrderStatusTerminalException(order.getId());
        }
        
        if (isOrderStatusPaid(order)) {
            throw new OrderAlreadyPaidException(order.getId());
        }
        
        order.setCurrentStatus(Constants.ORDER_STATUS_CANCELLED);

        publishOrderEvent(Constants.PUBLISH_CANCEL_ORDER_EVENT_NAME, request.getUserId(), order);

        return CancelOrderResponseDTO.builder()
                .id(request.getOrderId())
                .success(true)
                .build();
    };

    private boolean isOrderStatusPaid(Order order) {
        return order.getCurrentStatus().equals(Constants.ORDER_STATUS_PAID);
    }

    private boolean isOrderStatusTerminal(Order order) {
        Integer orderStatus = order.getCurrentStatus();

        boolean orderIsCancelled = orderStatus.equals(Constants.ORDER_STATUS_CANCELLED);
        boolean orderIsFailed = orderStatus.equals(Constants.ORDER_STATUS_FAILED);
        boolean orderIsShipped = orderStatus.equals(Constants.ORDER_STATUS_SHIPPED);

        if (orderIsCancelled || orderIsFailed || orderIsShipped) {
            return true;
        }

        return false;
    }

    private void publishOrderEvent(String eventName, Long userId, Order order) {
        String topic = Utils.DetermineTopic(orderMessagingStrategy, Constants.ORDERS_TOPIC_NAME);

        MessageEnvelopeDTO<String, Order> orderEventDto = new MessageEnvelopeDTO<String, Order>(
                topic,
                order.getId().toString(),
                order,
                Collections.emptyMap());

        String strategyName = orderMessagingStrategy.getClass().getSimpleName();
        Timer.Sample sample = Timer.start(meterRegistry);
        String outcome = "unknown";
        try {
            orderMessagingStrategy.publish(orderEventDto)
                    .whenComplete((result, exception) -> {
                        String finalOutcome = "unknown";
                        if (exception != null) {
                            log.error(String.format("Async publish failed for %s event: %s", eventName, exception.getMessage()));
                            finalOutcome = "failure";
                        } else if (result != null && result.isSuccess()) {
                            finalOutcome = "success";
                        } else {
                            finalOutcome = "completed_unknown";
                        }

                        sample.stop(meterRegistry.timer("messaging.publish.latency", 
                                "eventName", eventName,
                                        "strategy", strategyName,
                                        "topic", topic,
                                        "outcome", finalOutcome));
                    });
        } catch (Exception e) {
            log.error("Synchronous error during publish()");
            outcome = "error_sync";
            sample.stop(meterRegistry.timer("messaging.publish.latency",
                    "eventName", eventName,
                            "strategy", strategyName,
                            "topic", topic,
                            "outcome", outcome));
        }
    }
}
