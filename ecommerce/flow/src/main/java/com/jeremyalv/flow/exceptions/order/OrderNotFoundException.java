package com.jeremyalv.flow.exceptions.order;

import com.jeremyalv.flow.exceptions.common.ResourceNotFoundException;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(Long id) {
        super("Order", id);
    }
}
