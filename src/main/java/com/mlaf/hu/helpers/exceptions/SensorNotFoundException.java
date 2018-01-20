package com.mlaf.hu.helpers.exceptions;

public class SensorNotFoundException extends Exception {

    public SensorNotFoundException() {
        super();
    }

    public SensorNotFoundException(String sensorId) {
        super(sensorId);
    }
}
