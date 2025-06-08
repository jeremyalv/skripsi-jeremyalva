package com.jeremyalv.flow.exceptions.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jeremyalv.flow.exceptions.common.ResourceNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(Long id) {
        super("Product", id);
    }
}
