package com.agyohora.mobileperitc.exceptions;

public class ComponentFailureException extends RuntimeException {

    String message;

    public ComponentFailureException() {
        super();
    }

    public ComponentFailureException(String message) {
        super(message);

        this.message = message;
    }
}