package com.mlaf.hu.sensoragent;

import java.util.List;

public abstract class Sensor {
    private boolean active;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public abstract String getSensorID();

    public abstract List<SensorMeasurements> getMeasurements();

}
