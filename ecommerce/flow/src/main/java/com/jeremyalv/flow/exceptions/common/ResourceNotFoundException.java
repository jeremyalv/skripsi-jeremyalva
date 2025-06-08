package com.jeremyalv.flow.exceptions.common;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceType, Object id) {
        super(String.format("%s not found with ID: %s", resourceType, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
