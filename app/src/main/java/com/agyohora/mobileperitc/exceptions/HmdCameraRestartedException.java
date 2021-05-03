package com.agyohora.mobileperitc.exceptions;

public class HmdCameraRestartedException extends RuntimeException {

    String message;

    public HmdCameraRestartedException() {
        super();
    }

    public HmdCameraRestartedException(String message) {
        super(message);

        this.message = message;
    }
}