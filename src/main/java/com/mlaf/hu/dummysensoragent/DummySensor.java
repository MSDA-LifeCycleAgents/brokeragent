package com.mlaf.hu.dummysensoragent;

import com.mlaf.hu.models.Measurement;
import com.mlaf.hu.models.Measurements;
import com.mlaf.hu.sensoragent.Sensor;

import java.util.concurrent.ThreadLocalRandom;

public class DummySensor extends Sensor {
    String sensorID;

    public DummySensor(String idPostfix) {
        this.sensorID = "DummySensor" + idPostfix;
    }

    @Override
    public String getSensorID() {
        return this.sensorID;
    }

    @Override
    public Measurements getMeasurements() {
        Measurements measurements = new Measurements();
        measurements.addMeasurement(new Measurement("val1", getRandomNum()));
        measurements.addMeasurement(new Measurement("val2", getRandomNum()));

        return measurements;
    }

    private int getRandomNum() {
        int min = 0;
        int max = 100;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
