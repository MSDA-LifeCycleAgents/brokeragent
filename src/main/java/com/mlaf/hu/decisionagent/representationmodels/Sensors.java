package com.mlaf.hu.decisionagent.representationmodels;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "sensors")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sensors {
    @XmlElement(name = "sensor")
    private ArrayList<Sensor> sensors = null;

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }
}
