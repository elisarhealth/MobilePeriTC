package com.agyohora.mobileperitc.exceptions;

public class PrbException extends RuntimeException {

    String message;

    public PrbException() {
        super();
    }

    public PrbException(String message) {
        super(message);

        this.message = message;
    }
}
