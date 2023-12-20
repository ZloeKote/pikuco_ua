package com.pikuco.userservice.handler;

import com.pikuco.userservice.exception.ObjectNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleValidationException(ObjectNotValidException exception) {
        return ResponseEntity.badRequest().body(exception.getErrorMessages());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleValidationException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
