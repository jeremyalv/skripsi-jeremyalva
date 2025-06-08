package com.jeremyalv.flow.exceptions.order;

import com.jeremyalv.flow.exceptions.common.ResourceConflictException;

public class OrderAlreadyPaidException extends ResourceConflictException {
    public OrderAlreadyPaidException(Long id) {
        super("OrderPayment", id);
    }
}
