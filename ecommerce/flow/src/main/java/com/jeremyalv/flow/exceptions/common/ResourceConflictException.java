package com.jeremyalv.flow.exceptions.common;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String resourceType, Object id) {
        super(String.format("Found conflicting '%s' resource for resource Id: %s", resourceType, id));
    }

    public ResourceConflictException(String message) {
        super(message);
    }
}
