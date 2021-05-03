package com.agyohora.mobileperitc.exceptions;

public class AccessoryStatusException extends RuntimeException {

    String message;

    public AccessoryStatusException() {
        super();
    }

    public AccessoryStatusException(String message) {
        super(message);

        this.message = message;
    }
}
