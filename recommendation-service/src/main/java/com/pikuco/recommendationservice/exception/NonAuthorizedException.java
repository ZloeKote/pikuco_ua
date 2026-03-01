package com.pikuco.recommendationservice.exception;

public class NonAuthorizedException extends RuntimeException {
    public NonAuthorizedException(String message) {
        super(message);
    }
}


