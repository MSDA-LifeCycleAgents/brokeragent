package com.mlaf.hu.sensoragent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SensorReading {
    private HashMap<Sensor, List<SensorMeasurements>> sensorMeasurementHashMap = new HashMap<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public SensorReading() {

    }

    public void addSensorMeassurements(Sensor sensor, List<SensorMeasurements> measurements) {
        sensorMeasurementHashMap.put(sensor, measurements);
    }

    private long timestampInMilis() {
        //TODO Do we want this to be GMT?
        ZoneId zoneId = ZoneId.of("GMT");
        return timestamp.atZone(zoneId).toEpochSecond();
    }


    public String toXML() {
        //TODO Do we want an actual namespace document?
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            Element mainRootElement = doc.createElementNS("https://github.com/MSDA-LifeCycleAgents/SensorReadingsXML", "sensorreadings");
            doc.appendChild(mainRootElement);

            // Timestamp
            Element timestampElem = doc.createElement("timestamp");
            timestampElem.appendChild(doc.createTextNode(String.valueOf(timestampInMilis())));
            mainRootElement.appendChild(timestampElem);

            //Sensors
            Element sensorList = doc.createElement("sensors");
            for (Map.Entry<Sensor, List<SensorMeasurements>> entry : sensorMeasurementHashMap.entrySet()) {
                Sensor sensor = entry.getKey();
                Element sensorElem = doc.createElement("sensor");
                sensorElem.setAttribute("id", sensor.getSensorID());
                // Sensor Measurements
                for (SensorMeasurements sm: entry.getValue()) {
                    Element valueNode = doc.createElement("value");
                    valueNode.setAttribute("measurement", sm.getMeassurement());
                    valueNode.appendChild(doc.createTextNode(sm.getValue()));
                    sensorElem.appendChild(valueNode);
                }
                sensorList.appendChild(sensorElem);
            }
            mainRootElement.appendChild(sensorList);

            // Transform to string //TODO Move to another function
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            transformer.transform(source, new StreamResult(stringWriter));
            return stringWriter.toString();

        } catch (Exception e) {
            SensorAgent.sensorAgentLogger.log(Level.SEVERE, "Could not create XML", e);
            return null;
        }
    }
}
