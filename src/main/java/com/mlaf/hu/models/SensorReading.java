package com.mlaf.hu.models;

import com.mlaf.hu.models.Sensors;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "sensorreading")
public class SensorReading {
    private Sensors sensors;

    public Sensors getSensors() {
        return sensors;
    }

    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
    }
}
