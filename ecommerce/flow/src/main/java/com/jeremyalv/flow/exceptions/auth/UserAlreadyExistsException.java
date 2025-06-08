package com.jeremyalv.flow.exceptions.auth;

import com.jeremyalv.flow.exceptions.common.ResourceConflictException;

public class UserAlreadyExistsException extends ResourceConflictException {
    public UserAlreadyExistsException(String email) {
        super(String.format("User with email '%s' already exists", email));
    }
}
