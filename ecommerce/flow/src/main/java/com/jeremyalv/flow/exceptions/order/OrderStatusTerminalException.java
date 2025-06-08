package com.jeremyalv.flow.exceptions.order;

import com.jeremyalv.flow.exceptions.common.ResourceConflictException;

public class OrderStatusTerminalException extends ResourceConflictException {
    public OrderStatusTerminalException(Long id) {
        super("OrderStatusTerminal", id);
    }
}
