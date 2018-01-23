package com.mlaf.hu.helpers.exceptions;

public class SensorReadException extends Exception {
    public SensorReadException() {
        super();
    }

    public SensorReadException(String message) {
        super(message);
    }

    public SensorReadException(String message, Throwable t) {
        super(message, t);
    }

    public SensorReadException(Throwable t) {
        super(t);
    }
}
