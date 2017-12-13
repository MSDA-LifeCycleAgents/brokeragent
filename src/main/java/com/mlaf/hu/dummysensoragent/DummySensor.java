package com.mlaf.hu.dummysensoragent;

import com.mlaf.hu.sensoragent.Sensor;

import java.util.concurrent.ThreadLocalRandom;

public class DummySensor implements Sensor {
    String sensorID;

    public DummySensor(String idPostfix) {
        this.sensorID = "DummySensor" + idPostfix;
    }

    @Override
    public boolean activate() {
        return true;
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    @Override
    public String getSensorID() {
        return this.sensorID;
    }

    @Override
    public String getValue() {
        int min = 0;
        int max = 100;

        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return String.valueOf(randomNum);
    }

    @Override
    public String getUnit() {
        return "Randoms";
    }
}
