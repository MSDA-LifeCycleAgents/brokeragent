package com.mlaf.hu.implementationexample.sensoragent;
import com.mlaf.hu.sensoragent.InvalidSensorException;
import com.mlaf.hu.sensoragent.SensorAgent;
import com.mlaf.hu.sensoragent.Sensor;
import java.util.logging.Level;

public class SensorAgentImpl extends SensorAgent {

    public SensorAgentImpl() {
        super();
        Sensor s1 = new SensorImpl1();
        s1.activate();
        Sensor s2 = new SensorImpl2();
        s2.activate();
        try {
            addSensor(s1);
            addSensor(s2);
        } catch (InvalidSensorException e) {
            sensorAgentLogger.log(Level.WARNING, "Could not register sensor, reason: " + e.getMessage(), e);
        }
    }

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
                "    <amountOfMissedDataPackages>1</amountOfMissedDataPackages>\n" +
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
}
