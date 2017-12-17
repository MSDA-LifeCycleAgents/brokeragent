package com.mlaf.hu.sensoragent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SensorReading {
    private String sensorID;
    private String value;
    private String unit;

    public SensorReading(String id, String value, String unit) {
        this.sensorID = id;
        this.value = value;
        this.unit = unit;
    }

    public SensorReading(Sensor s) {
        this.sensorID = s.getSensorID();
        this.value = s.getValue();
        this.unit = s.getUnit();
    }

    public Node getReadingNode(Document doc) {
        Element reading = doc.createElement("reading");
        reading.setAttribute("id", sensorID);
        Element valueNode = doc.createElement("value");
        valueNode.appendChild(doc.createTextNode(value));
        Element unitNode = doc.createElement("unit");
        unitNode.appendChild(doc.createTextNode(unit));
        reading.appendChild(valueNode);
        reading.appendChild(unitNode);
        return reading;
    }
}
