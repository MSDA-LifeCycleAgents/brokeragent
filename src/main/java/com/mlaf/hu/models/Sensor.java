package com.mlaf.hu.models;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement(name = "sensor")
public class Sensor implements Serializable {
    private String id;
    private String label;
    private int intervalInSeconds;
    private int amountOfBackupMeasurements;
    private Measurements measurements;

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @XmlElement(name = "intervalinseconds")
    public int getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void setIntervalInSeconds(int intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;
    }

    @XmlElement
    public int getAmountOfBackupMeasurements() {
        return amountOfBackupMeasurements;
    }

    public void setAmountOfBackupMeasurements(int amountOfBackupMeasurements) {
        this.amountOfBackupMeasurements = amountOfBackupMeasurements;
    }

    public Measurements getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Measurements measurements) {
        this.measurements = measurements;
    }
}
