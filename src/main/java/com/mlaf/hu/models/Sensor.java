package com.mlaf.hu.models;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement ( name = "sensor")
public class Sensor{
    private String id;
    private String label;
    private int min, max;
    private int intervalInSeconds;
    private Plans plans;
    private int amountOfBackupMeasurements;
    private int value;
    private ArrayList<Integer> readings = new ArrayList<>();

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

    @XmlElement (name = "intervalinseconds")
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

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Plans getPlans() {
        return plans;
    }

    public void setPlans(Plans plans) {
        this.plans = plans;
    }

    @XmlElement
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ArrayList<Integer> getReadings() {
        return readings;
    }
}
