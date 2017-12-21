package com.mlaf.hu.sensoragent;

public class SensorMeasurements {
    private String meassurement;
    private String value;

    public SensorMeasurements(String meassurement, String value) {
        this.meassurement = meassurement;
        this.value = value;
    }

    public String getMeassurement() {
        return meassurement;
    }

    public String getValue() {
        return value;
    }
}
