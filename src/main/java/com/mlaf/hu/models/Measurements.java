package com.mlaf.hu.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement(name = "measurements")
@XmlAccessorType(XmlAccessType.FIELD)
public class Measurements implements Serializable {
    @XmlElement(name = "measurement")
    private ArrayList<Measurement> measurements = null;

    public Measurements() {
        measurements = new ArrayList<>();
    }

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ArrayList<Measurement> measurements) {
        this.measurements = measurements;
    }

    public Measurement getMeasurement(String id) {
        Measurement measurement = null;
        for(Measurement ms : this.measurements) {
            if(ms.getId().equals(id)) {
                measurement = ms;
            }
        }
        return  measurement;
    }

    public void addMeasurement(Measurement measurement) {
        this.measurements.add(measurement);
    }
}
