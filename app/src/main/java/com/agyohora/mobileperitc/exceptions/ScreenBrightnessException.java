package com.agyohora.mobileperitc.exceptions;

public class ScreenBrightnessException extends RuntimeException {

    String message;

    public ScreenBrightnessException() {
        super();
    }

    public ScreenBrightnessException(String message) {
        super(message);

        this.message = message;
    }
}