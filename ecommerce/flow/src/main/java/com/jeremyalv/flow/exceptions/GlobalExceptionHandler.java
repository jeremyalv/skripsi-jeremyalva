package com.jeremyalv.flow.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jeremyalv.flow.exceptions.auth.UserAlreadyExistsException;
import com.jeremyalv.flow.exceptions.common.ResourceConflictException;
import com.jeremyalv.flow.exceptions.common.ResourceNotFoundException;
import com.jeremyalv.flow.exceptions.order.OrderAlreadyPaidException;
import com.jeremyalv.flow.exceptions.order.OrderNotFoundException;
import com.jeremyalv.flow.exceptions.order.OrderStatusTerminalException;
import com.jeremyalv.flow.exceptions.product.ProductNotFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // Security Exception Handlers //

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setProperty("description", "Incorrect username or password.");
        return problemDetail;
    }

    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatusException(AccountStatusException ex) {
        log.warn("Account status issue: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Account Access Issue");
        problemDetail.setProperty("description", "Account is locked, disabled, or expired.");
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Access Denied");
        problemDetail.setProperty("description", "You are not authorized to access this resource.");
        return problemDetail;
    }

    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleJwtSignatureException(SignatureException ex) {
        log.warn("JWT signature validation failed: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Invalid Token Signature");
        problemDetail.setProperty("description", "The provided token signature is invalid.");
        return problemDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleJwtExpiredException(ExpiredJwtException ex) {
        log.warn("JWT token has expired: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Token Expired");
        problemDetail.setProperty("description", "The provided token has expired.");
        return problemDetail;
    }

    // Resource Not Found Handlers //

    @ExceptionHandler({
            ProductNotFoundException.class,
            OrderNotFoundException.class
    })
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: %s", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource not found");
        return problemDetail;
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            OrderAlreadyPaidException.class,
            OrderStatusTerminalException.class
    })
    public ProblemDetail handleResourceConflictException(ResourceConflictException ex) {
        log.warn("Resource conflict: ", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Resource conflict");
        return problemDetail;
    }

    // Generic Fallback Handler //

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }
}
