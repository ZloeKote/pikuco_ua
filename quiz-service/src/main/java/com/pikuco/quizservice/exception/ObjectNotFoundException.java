package com.pikuco.quizservice.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class ObjectNotFoundException extends RuntimeException {
    private final Set<String> errorMessages;

    public ObjectNotFoundException(Set<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
