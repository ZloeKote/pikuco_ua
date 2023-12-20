package com.pikuco.userservice.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class ObjectNotValidException extends RuntimeException {
    private final Set<String> errorMessages;

    public ObjectNotValidException(Set<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
