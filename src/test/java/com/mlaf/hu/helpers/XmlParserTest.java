package com.mlaf.hu.helpers;

import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.models.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class XmlParserTest {
    XmlParser helper;
    private String instructionXML, sensorReadingXML;

    @Before
    public void setUp() throws Exception {
        this.instructionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                            "<instructions>\n" +
                            "\t<identifier>fVTz7OCaD8WFJE5Jvw7K</identifier>\n" +
                            "\t<messaging>\n" +
                            "\t\t<topic>\n" +
                            "\t\t\t<name>sensor_agent#fVTz7OCaD8WFJE5Jvw7K</name>\n" +
                            "\t\t\t<daysToKeepMessages>1</daysToKeepMessages>\n" +
                            "\t\t</topic>\n" +
                            "\t\t<directToDecisionAgent>False</directToDecisionAgent>\n" +
                            "\t</messaging>\n" +
                            "\t<amountOfMissedDataPackages>5</amountOfMissedDataPackages>\n" +
                            "\t<sensors>\n" +
                            "\t\t<sensor id=\"SystolicBloodPressure\">\n" +
                            "\t\t\t<label>Systolic Blood Pressure</label>\n" +
                            "\t\t\t<intervalinseconds>30</intervalinseconds>\n" +
                            "\t\t\t<unit>mm Hg</unit>\n" +
                            "\t\t\t<measurements>\n" +
                            "\t\t\t\t<measurement id=\"y\">\n" +
                            "\t\t\t\t\t<min>0</min>\n" +
                            "\t\t\t\t\t<max>200</max>\n" +
                            "\t\t\t\t\t<plans>\n" +
                            "\t\t\t\t\t\t<plan>\n" +
                            "\t\t\t\t\t\t\t<below>0.6</below>\n" +
                            "\t\t\t\t\t\t\t<message>Watch out!</message>\n" +
                            "\t\t\t\t\t\t\t<via>ScreenAgent</via>\n" +
                            "\t\t\t\t\t\t\t<to></to>\n" +
                            "\t\t\t\t\t\t\t<limit>30</limit>\n" +
                            "\t\t\t\t\t\t</plan>\n" +
                            "\t\t\t\t\t\t<plan>\n" +
                            "\t\t\t\t\t\t\t<below>0.4</below>\n" +
                            "\t\t\t\t\t\t\t<message>Panic!</message>\n" +
                            "\t\t\t\t\t\t\t<via>MailAgent</via>\n" +
                            "\t\t\t\t\t\t\t<to>brian.vanderbijl@hu.nl</to>\n" +
                            "\t\t\t\t\t\t\t<limit>3600</limit>\n" +
                            "\t\t\t\t\t\t</plan>\n" +
                            "\t\t\t\t\t</plans>\n" +
                            "\t\t\t\t</measurement>\n" +
                            "\t\t\t\t<measurement id=\"x\">\n" +
                            "\t\t\t\t\t<min>0</min>\n" +
                            "\t\t\t\t\t<max>200</max>\n" +
                            "\t\t\t\t\t<plans>\n" +
                            "\t\t\t\t\t\t<plan>\n" +
                            "\t\t\t\t\t\t\t<below>0.6</below>\n" +
                            "\t\t\t\t\t\t\t<message>Watch out!</message>\n" +
                            "\t\t\t\t\t\t\t<via>ScreenAgent</via>\n" +
                            "\t\t\t\t\t\t\t<to></to>\n" +
                            "\t\t\t\t\t\t\t<limit>30</limit>\n" +
                            "\t\t\t\t\t\t</plan>\n" +
                            "\t\t\t\t\t\t<plan>\n" +
                            "\t\t\t\t\t\t\t<below>0.4</below>\n" +
                            "\t\t\t\t\t\t\t<message>Panic!</message>\n" +
                            "\t\t\t\t\t\t\t<via>MailAgent</via>\n" +
                            "\t\t\t\t\t\t\t<to>brian.vanderbijl@hu.nl</to>\n" +
                            "\t\t\t\t\t\t\t<limit>3600</limit>\n" +
                            "\t\t\t\t\t\t</plan>\n" +
                            "\t\t\t\t\t</plans>\n" +
                            "\t\t\t\t</measurement>\n" +
                            "\t\t\t</measurements>\n" +
                            "\t\t\t<amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                            "\t\t</sensor>\n" +
                            "\t\t<sensor id=\"HeartRate\">\n" +
                            "\t\t\t<label>Heart Rate</label>\n" +
                            "\t\t\t<unit>bpm</unit>\n" +
                            "\t\t\t<intervalinseconds>30</intervalinseconds>\n" +
                            "\t\t\t<measurements>\n" +
                            "\t\t\t\t<measurement id=\"only\">\n" +
                            "\t\t\t\t\t<min>0</min>\n" +
                            "\t\t\t\t\t<max>200</max>\n" +
                            "\t\t\t\t\t<plans>\n" +
                            "\t\t\t\t\t\t<plan>\n" +
                            "\t\t\t\t\t\t\t<below>0.6</below>\n" +
                            "\t\t\t\t\t\t\t<message>Watch out!</message>\n" +
                            "\t\t\t\t\t\t\t<via>ScreenAgent</via>\n" +
                            "\t\t\t\t\t\t\t<to></to>\n" +
                            "\t\t\t\t\t\t\t<limit>30</limit>\n" +
                            "\t\t\t\t\t\t</plan>\n" +
                            "\t\t\t\t\t\t<plan>\n" +
                            "\t\t\t\t\t\t\t<below>0.4</below>\n" +
                            "\t\t\t\t\t\t\t<message>Panic!</message>\n" +
                            "\t\t\t\t\t\t\t<via>MailAgent</via>\n" +
                            "\t\t\t\t\t\t\t<to>brian.vanderbijl@hu.nl</to>\n" +
                            "\t\t\t\t\t\t\t<limit>3600</limit>\n" +
                            "\t\t\t\t\t\t</plan>\n" +
                            "\t\t\t\t\t</plans>\n" +
                            "\t\t\t\t</measurement>\n" +
                            "\t\t\t</measurements>\n" +
                            "\t\t\t<amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                            "\t\t</sensor>\n" +
                            "\t</sensors>\n" +
                            "\t<fallback>\n" +
                            "\t\t<via>ScreenAgent</via>\n" +
                            "\t\t<to></to>\n" +
                            "\t</fallback>\n" +
                            "</instructions>";
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