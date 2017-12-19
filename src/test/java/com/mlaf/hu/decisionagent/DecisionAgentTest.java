package com.mlaf.hu.decisionagent;

import jade.core.AID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DecisionAgentTest {
    private String instructionXML;
    private String sensorReadingXML;
    private DecisionAgent da;


    @Before
    public void setUp() throws Exception {
        this.instructionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "    <instructions>\n" +
                "        <identifier>fVTz7OCaD8WFJE5Jvw7K</identifier>\n" +
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
                "                <amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n"+
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
                "                <amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                "            </sensor>\n" +
                "        </sensors>\n" +
                "        <fallback>\n" +
                "            <via>ScreenAgent</via>\n" +
                "            <to></to>\n" +
                "        </fallback>\n" +
                "    </instructions>\n";
        this.sensorReadingXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                                "<sensorreading>\n" +
                                "\t<sensors>\n" +
                                "\t\t<sensor id=\"HeartRate\">\n" +
                                "\t\t\t<value>133</value>\n" +
                                "\t\t</sensor>\n" +
                                "\t\t<sensor id=\"SystolicBloodPressure\">\n" +
                                "\t\t\t<value>113</value>\n" +
                                "\t\t</sensor>\n" +
                                "\t</sensors>\n" +
                                "</sensorreading>";
        this.da = new DecisionAgent() {
            @Override
            public void unregisterSensorAgent(AID sensoragent) {

            }

            @Override
            public void storeReading(int value) {

            }
        };
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseInstructionXml() {
        assert this.instructionXML != null;
        InstructionSet is = this.da.parseInstructionXml(this.instructionXML);
        assert is.getIdentifier().equals("fVTz7OCaD8WFJE5Jvw7K");
        assert is.getSensors().getSensors().get(1).getLabel().equals("Heart Rate");
        assert is.getSensors().getSensors().get(0).getPlans().getPlans().get(1).getVia().equals("MailAgent");
    }

    @Test
    public void parseSensorReadingXml() {
        assert this.sensorReadingXML != null;
        SensorReading sr = this.da.parseSensorReadingXml(this.sensorReadingXML);
        assert sr.getSensors().getSensors().get(0).getId().equals("HeartRate");
        assert sr.getSensors().getSensors().get(1).getValue() == 113;
    }
}