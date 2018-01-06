package com.mlaf.hu.models;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement ( name = "measurement")
public class Measurement {
    private String id;
    private Plans plans;
    private int min, max;
    private int value;
    private ArrayList<Double> readings = new ArrayList<>();

    public Plans getPlans() {
        return plans;
    }

    public void setPlans(Plans plans) {
        this.plans = plans;
    }

    @XmlElement(name = "min")
    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @XmlElement(name = "max")
    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @XmlElement(name = "value")
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ArrayList<Double> getReadings() {
        return readings;
    }

    public void setReadings(ArrayList<Double> readings) {
        this.readings = readings;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
