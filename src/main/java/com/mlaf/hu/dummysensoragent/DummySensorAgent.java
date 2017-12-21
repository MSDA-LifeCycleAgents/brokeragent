package com.mlaf.hu.dummysensoragent;

import com.mlaf.hu.sensoragent.Sensor;
import com.mlaf.hu.sensoragent.SensorAgent;

public class DummySensorAgent extends SensorAgent {
    public DummySensorAgent() {
        super(1000);
        Sensor dummy1 = new DummySensor("1");
        Sensor dummy2 = new DummySensor("2");
        dummy1.activate();
        addSensor(dummy1);
        dummy2.activate();
        addSensor(dummy2);
    }

    @Override
    public String getInstructionXML() {
        return "<xml>\n" +
                "    <instructions>\n" +
                "        <identiefer>\n" +
                "            fVTz7OCaD8WFJE5Jvw7K\n" +
                "        </identiefer>\n" +
                "        <messaging>\n" +
                "            <topic>\n" +
                "                <name>sensor_agent#fVTz7OCaD8WFJE5Jvw7K</name>\n" +
                "                <daysToKeepMessages>1</daysToKeepMessages>\n" +
                "            </topic>\n" +
                "            <directToDecisionAgent>False</directToDecisionAgent>\n" +
                "        </messaging>\n" +
                "        <sensors>\n" +
                "            <sensor id=\"SystolicBloodPressure\">\n" +
                "                <label>Systolic Blood Pressure</label>\n" +
                "                <min>0</min>\n" +
                "                <max>200</max>\n" +
                "                <unit>mm Hg</unit>\n" +
                "                <intervalinseconds>30</intervalinseconds>\n" +
                "                <plans>\n" +
                "                    <plan>\n" +
                "                        <below>0.6</below>\n" +
                "                        <message>Watch out!</message>\n" +
                "                        <via>ScreenAgent</via>\n" +
                "                        <to></to>\n" +
                "                        <limit>30</limit>\n" +
                "                    </plan>\n" +
                "                    <plan>\n" +
                "                        <below>0.4</below>\n" +
                "                        <message>Panic!</message>\n" +
                "                        <via>MailAgent</via>\n" +
                "                        <to>brian.vanderbijl@hu.nl</to>\n" +
                "                        <limit>3600</limit>\n" +
                "                    </plan>\n" +
                "                </plans>\n" +
                "                <backupMeasurements>\n" +
                "                    <amount>20</amount>\n" +
                "                </backupMeasurements>\n" +
                "            </sensor>\n" +
                "            <sensor id=\"HeartRate\">\n" +
                "                <label>Heart Rate</label>\n" +
                "                <min>0</min>\n" +
                "                <max>200</max>\n" +
                "                <unit>bpm</unit>\n" +
                "                <intervalinseconds>30</intervalinseconds>\n" +
                "                <plans>\n" +
                "                    <plan>\n" +
                "                        <below>0.6</below>\n" +
                "                        <message>Watch out!</message>\n" +
                "                        <via>ScreenAgent</via>\n" +
                "                        <to></to>\n" +
                "                        <limit>30</limit>\n" +
                "                    </plan>\n" +
                "                    <plan>\n" +
                "                        <below>0.4</below>\n" +
                "                        <message>Panic!</message>\n" +
                "                        <via>MailAgent</via>\n" +
                "                        <to>brian.vanderbijl@hu.nl</to>\n" +
                "                        <limit>3600</limit>\n" +
                "                    </plan>\n" +
                "                </plans>\n" +
                "                <backupMeasurements>\n" +
                "                    <amount>20</amount>\n" +
                "                </backupMeasurements>\n" +
                "            </sensor>\n" +
                "        </sensors>\n" +
                "        <fallback>\n" +
                "            <via>ScreenAgent</via>\n" +
                "            <to></to>\n" +
                "        </fallback>\n" +
                "    </instructions>\n" +
                "</xml>";
    }
}
