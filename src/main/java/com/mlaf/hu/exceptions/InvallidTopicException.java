package com.mlaf.hu.exceptions;

public class InvallidTopicException extends Exception {
    public InvallidTopicException(String message) {
        super(message);
    }

    public InvallidTopicException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
