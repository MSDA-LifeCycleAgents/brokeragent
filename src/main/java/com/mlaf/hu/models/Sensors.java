package com.mlaf.hu.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement(name = "sensors")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sensors implements Serializable {
    @XmlElement(name = "sensor")
    private ArrayList<Sensor> sensors = null;

    public Sensors() {
        this.sensors = new ArrayList<>();
    }

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor) {
        this.sensors.add(sensor);
    }

    public boolean isEmpty() {
        return this.sensors.isEmpty();
    }
}
