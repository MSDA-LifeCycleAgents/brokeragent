package com.mlaf.hu.brokeragent.exceptions;

public class TopicNotManagedException extends Exception {
    public TopicNotManagedException(String message) {
        super(message);
    }

    public TopicNotManagedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
