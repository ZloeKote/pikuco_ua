package com.pikuco.evaluationservice.handler;

import com.pikuco.evaluationservice.exception.NonAuthorizedException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NonAuthorizedException.class)
    public ResponseEntity<?> handleValidationException(NonAuthorizedException exception) {
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(exception.getErrorMessages());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleValidationException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
