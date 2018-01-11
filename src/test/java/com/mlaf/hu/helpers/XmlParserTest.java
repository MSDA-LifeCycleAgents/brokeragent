package com.mlaf.hu.helpers;

import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.models.SensorReading;
import org.junit.Before;
import org.junit.Test;

public class XmlParserTest {
    XmlParser helper;
    String instructionXML, sensorReadingXML;

    @Before
    public void setUp() throws Exception {
        this.instructionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<instructions>\n" +
                "    <identifier>fVTz7OCaD8WFJE5Jvw7K</identifier>\n" +
                "    <messaging>\n" +
                "        <topic>\n" +
                "            <name>sensor_agent#fVTz7OCaD8WFJE5Jvw7K</name>\n" +
                "            <daysToKeepMessages>1</daysToKeepMessages>\n" +
                "        </topic>\n" +
                "        <directToDecisionAgent>False</directToDecisionAgent>\n" +
                "    </messaging>\n" +
                "    <amountOfMissedDataPackages>5</amountOfMissedDataPackages>\n" +
                "    <sensors>\n" +
                "        <sensor id=\"SystolicBloodPressure\">\n" +
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
                "                            <message>Watch out!</message>\n" +
                "                            <via>ScreenAgent</via>\n" +
                "                            <to></to>\n" +
                "                            <limit>30</limit>\n" +
                "                        </plan>\n" +
                "                        <plan>\n" +
                "                            <below>0.4</below>\n" +
                "                            <message>Panic!</message>\n" +
                "                            <via>MailAgent</via>\n" +
                "                            <to>brian.vanderbijl@hu.nl</to>\n" +
                "                            <limit>3600</limit>\n" +
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
                "                            <via>ScreenAgent</via>\n" +
                "                            <to></to>\n" +
                "                            <limit>30</limit>\n" +
                "                        </plan>\n" +
                "                        <plan>\n" +
                "                            <below>0.4</below>\n" +
                "                            <message>Panic!</message>\n" +
                "                            <via>MailAgent</via>\n" +
                "                            <to>brian.vanderbijl@hu.nl</to>\n" +
                "                            <limit>3600</limit>\n" +
                "                        </plan>\n" +
                "                    </plans>\n" +
                "                </measurement>\n" +
                "            </measurements>\n" +
                "            <amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                "        </sensor>\n" +
                "        <sensor id=\"HeartRate\">\n" +
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
                "                            <via>ScreenAgent</via>\n" +
                "                            <to></to>\n" +
                "                            <limit>30</limit>\n" +
                "                        </plan>\n" +
                "                        <plan>\n" +
                "                            <below>0.4</below>\n" +
                "                            <message>Panic!</message>\n" +
                "                            <via>MailAgent</via>\n" +
                "                            <to>brian.vanderbijl@hu.nl</to>\n" +
                "                            <limit>3600</limit>\n" +
                "                        </plan>\n" +
                "                    </plans>\n" +
                "                </measurement>\n" +
                "            </measurements>\n" +
                "            <amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                "        </sensor>\n" +
                "    </sensors>\n" +
                "    <fallback>\n" +
                "        <via>ScreenAgent</via>\n" +
                "        <to></to>\n" +
                "    </fallback>\n" +
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
    public void unparse() {
        SensorReading sr = null;
        try {
            sr = XmlParser.parseToObject(SensorReading.class, this.sensorReadingXML);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert sr != null;

    }

    @Test
    public void parse() {
    }
}