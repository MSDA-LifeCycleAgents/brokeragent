package com.mlaf.hu.sensoragent;

public interface Sensor {
    public boolean activate();
    public boolean deactivate();
    public String getSensorID();
    public String getValue();
    public String getUnit();
}
