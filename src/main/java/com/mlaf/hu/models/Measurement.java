package com.mlaf.hu.models;

import javax.xml.bind.annotation.*;

import com.mlaf.hu.helpers.Configuration;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.Serializable;


@XmlRootElement(name = "measurement")
public class Measurement  implements Serializable {
    private static Configuration config = Configuration.getInstance();
    private String id;
    private Plans plans;
    private int min, max;
    private double value;
    private CircularFifoQueue<Double> readings = new CircularFifoQueue<>(Integer.parseInt(config.getProperty("instructionset.num_readings_in_memory")));

    public Measurement() {}

    public Measurement(String id, int value) {
        this.id = id;
        this.value = value;
    }

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
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public CircularFifoQueue<Double> getReadings() {
        return readings;
    }

    public void setReadings(CircularFifoQueue<Double> readings) {
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
