package com.pikuco.quizservice.handler;

import com.pikuco.quizservice.exception.NonAuthorizedException;
import com.pikuco.quizservice.exception.ObjectNotFoundException;
import com.pikuco.quizservice.exception.ObjectNotValidException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleValidationException(ObjectNotValidException exception) {
        return ResponseEntity.badRequest().body(exception.getErrorMessages());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<?> handleValidationException(ObjectNotFoundException exception) {
        return ResponseEntity.badRequest().body(exception.getErrorMessages());
    }

    @ExceptionHandler(NonAuthorizedException.class)
    public ResponseEntity<?> handleValidationException(NonAuthorizedException exception) {
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(exception.getErrorMessages());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleValidationException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
