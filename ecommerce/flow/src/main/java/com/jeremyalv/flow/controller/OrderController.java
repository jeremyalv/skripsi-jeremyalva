package com.jeremyalv.flow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeremyalv.flow.dto.order.request.CancelOrderRequestDTO;
import com.jeremyalv.flow.dto.order.request.CreateOrderRequestDTO;
import com.jeremyalv.flow.dto.order.request.GetOrderDetailRequestDTO;
import com.jeremyalv.flow.dto.order.request.GetUserOrdersRequestDTO;
import com.jeremyalv.flow.dto.order.request.PayOrderRequestDTO;
import com.jeremyalv.flow.dto.order.response.CreateOrderResponseDTO;
import com.jeremyalv.flow.dto.order.response.GetOrderDetailResponseDTO;
import com.jeremyalv.flow.dto.order.response.GetUserOrdersResponseDTO;
import com.jeremyalv.flow.dto.order.response.PayOrderResponseDTO;
import com.jeremyalv.flow.exceptions.auth.UserNotFoundException;
import com.jeremyalv.flow.model.User;
import com.jeremyalv.flow.repository.UserRepository;
import com.jeremyalv.flow.service.order.OrderService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<CreateOrderResponseDTO> createOrder(
            @RequestBody @Valid CreateOrderRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        request.setUserId(getUserIdFromUserDetails(userDetails));

        CreateOrderResponseDTO response = orderService.createOrder(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetOrderDetailResponseDTO> getOrderDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserIdFromUserDetails(userDetails);
        GetOrderDetailRequestDTO request = GetOrderDetailRequestDTO.builder()
                .orderId(id)
                .userId(userId)
                .build();
        
        GetOrderDetailResponseDTO response = orderService.getOrderDetail(request);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GetUserOrdersResponseDTO> getUserOrders(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long authenticatedUserId = getUserIdFromUserDetails(userDetails);
        if (!(userId.equals(authenticatedUserId))) {
            throw new AccessDeniedException("Cannot view orders for another user");
        }

        GetUserOrdersRequestDTO request = GetUserOrdersRequestDTO.builder().userId(userId).build();
        GetUserOrdersResponseDTO response = orderService.getUserOrders(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<PayOrderResponseDTO> payForOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        PayOrderRequestDTO request = PayOrderRequestDTO.builder()
                .orderId(id)
                .build();
        
        request.setUserId(getUserIdFromUserDetails(userDetails));

        PayOrderResponseDTO response = orderService.payForOrder(request);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @RequestBody @Valid CancelOrderRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        request.setOrderId(id);

        request.setUserId(getUserIdFromUserDetails(userDetails));

        orderService.cancelOrder(request);

        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));
    }
}
