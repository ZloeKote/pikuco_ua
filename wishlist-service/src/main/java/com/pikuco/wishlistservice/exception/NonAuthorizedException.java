package com.pikuco.wishlistservice.exception;

import lombok.Getter;

@Getter
public class NonAuthorizedException extends RuntimeException {
    private final String errorMessages;

    public NonAuthorizedException(String errorMessages) {
        this.errorMessages = errorMessages;
    }
}
