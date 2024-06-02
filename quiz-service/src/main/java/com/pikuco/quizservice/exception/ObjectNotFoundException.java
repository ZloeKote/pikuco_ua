package com.pikuco.quizservice.exception;

import lombok.Getter;

@Getter
public class ObjectNotFoundException extends RuntimeException {
    private final String errorMessage;

    public ObjectNotFoundException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
