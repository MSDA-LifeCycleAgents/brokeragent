package com.mlaf.hu.implementationexample.sensoragent;

import com.mlaf.hu.models.Measurement;
import com.mlaf.hu.models.Measurements;

import java.util.concurrent.ThreadLocalRandom;

public class SensorImpl2 extends com.mlaf.hu.sensoragent.Sensor {
    private String sensorID;

    public SensorImpl2() {
        super(10);
        this.sensorID = "DummySensor2";
    }

    @Override
    public String getSensorID() {
        return this.sensorID;
    }

    @Override
    public Measurements getMeasurements() {
        Measurements measurements = new Measurements();
        measurements.addMeasurement(new Measurement("only", getRandomNum()));
        return measurements;
    }

    private int getRandomNum() {
        int min = 0;
        int max = 100;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
