package com.mlaf.hu.sensoragent;

import com.mlaf.hu.implementationexample.sensoragent.SensorImpl1;
import com.mlaf.hu.implementationexample.sensoragent.SensorImpl2;
import com.mlaf.hu.models.Sensor;
import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.models.Sensors;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Level;

import static org.junit.Assert.*;

public class SensorAgentTest {
    private SensorAgent sensorAgent;

    @Before
    public void setUp() throws Exception {
        sensorAgent = new SensorAgent() {
            @Override
            protected String getInstructionXML() {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                        "<instructions>\n" +
                        "    <identifier>\n" +
                        "        fVTz7OCaD8WFJE5Jvw7K\n" +
                        "    </identifier>\n" +
                        "    <messaging>\n" +
                        "        <topic>\n" +
                        "            <name>sensor_agent#fVTz7OCaD8WFJE5Jvw7K</name>\n" +
                        "            <daysToKeepMessages>1</daysToKeepMessages>\n" +
                        "        </topic>\n" +
                        "        <directToDecisionAgent>false</directToDecisionAgent>\n" +
                        "    </messaging>\n" +
                        "    <amountOfMissedDataPackages>5</amountOfMissedDataPackages>\n" +
                        "    <sensors>\n" +
                        "        <sensor id=\"DummySensor1\">\n" +
                        "            <label>Systolic Blood Pressure</label>\n" +
                        "            <intervalinseconds>30</intervalinseconds>\n" +
                        "            <unit>mm Hg</unit>\n" +
                        "            <measurements>\n" +
                        "                <measurement id=\"y\">\n" +
                        "                    <min>0</min>\n" +
                        "                    <max>200</max>\n" +
                        "                    <plans>\n" +
                        "                        <plan>\n" +
                        "                            <below>0.6</below>\n" +
                        "                            <above>0.6</above>\n" +
                        "                            <message>Watch out!</message>\n" +
                        "                            <via>SlackAgent</via>\n" +
                        "                            <to></to>\n" +
                        "                            <limit>3</limit>\n" +
                        "                        </plan>\n" +
                        "                        <plan>\n" +
                        "                            <below>0.4</below>\n" +
                        "                            <above>0.4</above>\n" +
                        "                            <message>Panic! Measurement Y, Sensor: DummySensor1, Value: </message>\n" +
                        "                            <via>MailAgent</via>\n" +
                        "                            <to>thijs.gelton@hotmail.com</to>\n" +
                        "                            <limit>3</limit>\n" +
                        "                        </plan>\n" +
                        "                    </plans>\n" +
                        "                </measurement>\n" +
                        "                <measurement id=\"x\">\n" +
                        "                    <min>0</min>\n" +
                        "                    <max>200</max>\n" +
                        "                    <plans>\n" +
                        "                        <plan>\n" +
                        "                            <below>0.6</below>\n" +
                        "                            <message>Watch out!</message>\n" +
                        "                            <via>SlackAgent</via>\n" +
                        "                            <to>#general</to>\n" +
                        "                            <limit>30</limit>\n" +
                        "                        </plan>\n" +
                        "                        <plan>\n" +
                        "                            <below>0.4</below>\n" +
                        "                            <message>Panic! Measurement X, Sensor: DummySensor1, Value: </message>\n" +
                        "                            <via>MailAgent</via>\n" +
                        "                            <to>thijs.gelton@hotmail.com</to>\n" +
                        "                            <limit>3</limit>\n" +
                        "                        </plan>\n" +
                        "                    </plans>\n" +
                        "                </measurement>\n" +
                        "            </measurements>\n" +
                        "            <amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                        "        </sensor>\n" +
                        "        <sensor id=\"DummySensor2\">\n" +
                        "            <label>Heart Rate</label>\n" +
                        "            <unit>bpm</unit>\n" +
                        "            <intervalinseconds>30</intervalinseconds>\n" +
                        "            <measurements>\n" +
                        "                <measurement id=\"only\">\n" +
                        "                    <min>0</min>\n" +
                        "                    <max>200</max>\n" +
                        "                    <plans>\n" +
                        "                        <plan>\n" +
                        "                            <below>0.6</below>\n" +
                        "                            <message>Watch out!</message>\n" +
                        "                            <via>SlackAgent</via>\n" +
                        "                            <to>#general</to>\n" +
                        "                            <limit>30</limit>\n" +
                        "                        </plan>\n" +
                        "                        <plan>\n" +
                        "                            <below>0.4</below>\n" +
                        "                            <message>Panic! Measurement only, Sensor: DummySensor2, Value: </message>\n" +
                        "                            <via>MailAgent</via>\n" +
                        "                            <to>thijs.gelton@hotmail.com</to>\n" +
                        "                            <limit>3</limit>\n" +
                        "                        </plan>\n" +
                        "                    </plans>\n" +
                        "                </measurement>\n" +
                        "            </measurements>\n" +
                        "            <amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                        "        </sensor>\n" +
                        "    </sensors>\n" +
                        "    <fallback>\n" +
                        "        <message>This is the fallback message. \\nThe sensor_agent#fVTz7OCaD8WFJE5Jvw7K is unregistered.</message>" +
                        "        <via>MailAgent</via>\n" +
                        "        <to>thijs.gelton@hotmail.com</to>\n" +
                        "    </fallback>\n" +
                        "</instructions>";
            }

            @Override
            public void onReceivingRefuseRegistration() {
                this.doDelete();
            }
        };
    }

    @Test
    public void addSensorReadingToSendQueue() {
        SensorReading sr = new SensorReading();
        this.sensorAgent.addSensorReadingToSendQueue(sr);
        assertTrue(this.sensorAgent.getSensorReadingQueue().isEmpty());
        Sensors sensors = new Sensors();
        sensors.addSensor(new Sensor());
        sr.setSensors(sensors);
        this.sensorAgent.addSensorReadingToSendQueue(sr);
        assertTrue(this.sensorAgent.getSensorReadingQueue().size() > 0);
    }

    @Test
    public void getInstructionSet() {
        assertTrue(this.sensorAgent.getInstructionSet() != null);
    }
}