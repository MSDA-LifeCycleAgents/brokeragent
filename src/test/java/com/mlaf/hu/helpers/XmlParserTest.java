package com.mlaf.hu.helpers;

import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.models.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class XmlParserTest {
    XmlParser helper;
    private String sensorReadingXML;

    @Before
    public void setUp() throws Exception {
        this.sensorReadingXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                                "<sensorreading>\n" +
                                "\t<sensors>\n" +
                                "\t\t<sensor id=\"HeartRate\">\n" +
                                "\t\t\t<measurements>\n" +
                                "\t\t\t\t<measurement id=\"x\">\n" +
                                "\t\t\t\t\t<value>133</value>\n" +
                                "\t\t\t\t</measurement>\n" +
                                "\t\t\t\t<measurement id=\"y\">\n" +
                                "\t\t\t\t\t<value>122</value>\n" +
                                "\t\t\t\t</measurement>\n" +
                                "\t\t\t</measurements>\n" +
                                "\t\t</sensor>\n" +
                                "\t\t<sensor id=\"SystolicBloodPressure\">\n" +
                                "\t\t\t<measurements>\n" +
                                "\t\t\t\t<measurement id=\"henk\">\n" +
                                "\t\t\t\t\t<value>113</value>\n" +
                                "\t\t\t\t</measurement>\n" +
                                "\t\t\t</measurements>\n" +
                                "\t\t</sensor>\n" +
                                "\t</sensors> \n" +
                                "</sensorreading>";
    }

    @Test
    public void parseToObject() {
        SensorReading sr = null;
        try {
            sr = XmlParser.parseToObject(SensorReading.class, this.sensorReadingXML);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert sr != null;

    }

    @Test
    public void parseToXML() {
        SensorReading sr = new SensorReading();
        Sensors sensors = new Sensors();
        ArrayList<Sensor> sensorArrayList = new ArrayList<>();
        Sensor sensor = new Sensor();
        sensor.setIntervalInSeconds(30);
        sensor.setLabel("TEST");
        Measurements measurements = new Measurements();
        ArrayList<Measurement> measurementArrayList = new ArrayList<>();
        Measurement measurement = new Measurement();
        measurement.setMax(200);
        measurement.setMin(50);
        measurement.setId("Y");
        measurements.setMeasurements(measurementArrayList);
        sensor.setMeasurements(measurements);
        sensorArrayList.add(sensor);
        sensors.setSensors(sensorArrayList);
        sr.setSensors(sensors);
        String XMLString = null;
        try {
            XMLString = XmlParser.parseToXml(sr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert XMLString != null;
    }
}