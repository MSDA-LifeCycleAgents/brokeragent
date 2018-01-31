package com.mlaf.hu.sensoragent;

public class InvalidSensorException extends Exception {
    public InvalidSensorException(){
        super();
    }

    public InvalidSensorException(String message) {
        super(message);
    }
}
