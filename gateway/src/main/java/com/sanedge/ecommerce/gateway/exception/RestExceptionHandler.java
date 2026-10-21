package com.sanedge.ecommerce.gateway.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;

/**
 * Translates downstream gRPC failures and local validation errors onto the
 * HTTP status codes the former monolith exposed.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleGrpc(StatusRuntimeException ex) {
        Status.Code code = ex.getStatus().getCode();
        HttpStatus status;
        switch (code) {
            case NOT_FOUND -> status = HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT, FAILED_PRECONDITION -> status = HttpStatus.BAD_REQUEST;
            case ALREADY_EXISTS -> status = HttpStatus.CONFLICT;
            case UNAUTHENTICATED -> status = HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> status = HttpStatus.FORBIDDEN;
            default -> status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return build(status, ex.getStatus().getDescription() != null
                ? ex.getStatus().getDescription()
                : "gRPC error: " + code);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ ConstraintViolationException.class, MethodArgumentNotValidException.class,
            IllegalArgumentException.class })
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        String message = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException manv && manv.getBindingResult().getFieldError() != null) {
            message = manv.getBindingResult().getFieldError().getDefaultMessage();
        }
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupported(UnsupportedOperationException ex) {
        return build(HttpStatus.NOT_IMPLEMENTED, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "error");
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
