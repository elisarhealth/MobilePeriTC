package com.agyohora.mobileperitc.exceptions;

public class DailyNumberOfReportsException extends RuntimeException {

    String message;

    public DailyNumberOfReportsException() {
        super();
    }

    public DailyNumberOfReportsException(String message) {
        super(message);

        this.message = message;
    }
}