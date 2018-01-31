package com.mlaf.hu.models;

import com.mlaf.hu.helpers.exceptions.SensorNotFoundException;
import com.mlaf.hu.models.Sensors;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement (name = "sensorreading")
public class SensorReading implements Serializable {
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

    public Sensor getSensor(String sensorId) throws SensorNotFoundException {
        for(Sensor sensor: sensors.getSensors()) {
            if (sensor.getId().equals(sensorId)) {
                return sensor;
            }
        }
        throw new SensorNotFoundException(sensorId);
    }
}
