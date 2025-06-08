package com.jeremyalv.flow.exceptions.auth;

import com.jeremyalv.flow.exceptions.common.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String email) {
        super("User", email);
    }

    public UserNotFoundException(Long id) {
        super("User", id);
    }
}
