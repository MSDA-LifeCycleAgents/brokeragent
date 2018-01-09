package com.mlaf.hu.helpers.exceptions;

public class RelevantException extends Exception {
    public RelevantException(String message) {
        super(message);
    }

    public RelevantException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
