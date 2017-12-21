package com.mlaf.hu.dummysensoragent;

import com.mlaf.hu.sensoragent.Sensor;
import com.mlaf.hu.sensoragent.SensorMeasurements;

import java.util.ArrayList;
import java.util.List;
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
    public List<SensorMeasurements> getMeasurements() {
        ArrayList<SensorMeasurements> measurements = new ArrayList<>();
        measurements.add(new SensorMeasurements("val1", Integer.toString(getRandomNum())));
        measurements.add(new SensorMeasurements("val2", Integer.toString(getRandomNum())));
        return measurements;
    }

    private int getRandomNum() {
        int min = 0;
        int max = 100;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
