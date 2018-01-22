package com.mlaf.hu.sensoragent;

import com.mlaf.hu.models.Measurements;

import java.time.LocalDateTime;

public abstract class Sensor {
    private boolean active;
    private LocalDateTime lastRead;
    private int readInterval;

    public Sensor(int readInterval) {
        lastRead = LocalDateTime.now();
        this.readInterval = readInterval;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public abstract String getSensorID();

    public abstract Measurements getMeasurements();

    public boolean mustBeRead() {
        return (LocalDateTime.now().isAfter(lastRead.plusSeconds(readInterval)));
    }

    public void markRead() {
        lastRead = LocalDateTime.now();
    }

    public com.mlaf.hu.models.Sensor toDataSensor() {
        com.mlaf.hu.models.Sensor dataSensor = new com.mlaf.hu.models.Sensor();
        dataSensor.setId(getSensorID());
        dataSensor.setMeasurements(getMeasurements());
        return dataSensor;

    }

}
