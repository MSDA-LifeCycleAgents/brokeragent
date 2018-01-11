package com.mlaf.hu.models;

import com.mlaf.hu.models.Sensors;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "sensorreading")
public class SensorReading {
    private Sensors sensors;

    public SensorReading() {
        this.sensors = new Sensors();
    }

    public Sensors getSensors() {
        return sensors;
    }

    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor) {
        this.sensors.addSensor(sensor);
    }

    public boolean isEmpty() {
        return this.sensors.isEmpty();
    }
}
