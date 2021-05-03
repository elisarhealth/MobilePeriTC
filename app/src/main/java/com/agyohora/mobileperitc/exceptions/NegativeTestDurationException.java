package com.agyohora.mobileperitc.exceptions;

public class NegativeTestDurationException extends RuntimeException {

    String message;

    public NegativeTestDurationException() {
        super();
    }

    public NegativeTestDurationException(String message) {
        super(message);

        this.message = message;
    }
}