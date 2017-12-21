package com.mlaf.hu.decisionagent.behavior;

import com.mlaf.hu.brokeragent.Topic;
import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.decisionagent.InstructionSet;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

@Ignore
public class ReceiveBehaviorTest extends TestCase {
    private ReceiveBehavior RB;
    private Topic topic;
    private DecisionAgent da;
    private String instructionXML;

    @Before
    public void setUp() throws Exception {
        this.topic = new Topic(1);
        this.topic.setTopicName("JADE");
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
        this.da = new DecisionAgent() {
            @Override
            public void unregisterSensorAgent(AID sensoragent) {

            }

            @Override
            public void storeReading(int value) {

            }
        };
        this.RB = new ReceiveBehavior(this.da);
    }

    @Test
    public void testTopicMarshalling() {
        assert this.RB != null;
        assert this.topic != null;
        assert this.topic.getTopicName() != null;
        assert this.topic.getDaysToKeepMessages() > 0;
        String topicXML = this.RB.marshalTopic(this.topic);
        assert topicXML.startsWith("<?xml");
        assert topicXML.contains("<daysToKeepMessages>1</daysToKeepMessages>");
        assert topicXML.contains("<name>JADE</name>");

    }

    @Test
    public void testRequestFromTopic () {
        InstructionSet is = this.da.parseInstructionXml(this.instructionXML);
        ACLMessage message = this.RB.requestFromTopic(new AID("TEST", true), is);
        assert message.getPerformative() == ACLMessage.REQUEST;
        System.out.println(message.getContent());
        assert message.getContent().contains("<daysToKeepMessages>1</daysToKeepMessages>");
    }

    private static Object genericInvokMethod(Object obj, String methodName,
                                             int paramCount, Object... params) {
        Method method;
        Object requiredObj = null;
        Object[] parameters = new Object[paramCount];
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            parameters[i] = params[i];
            classArray[i] = params[i].getClass();
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return requiredObj;
    }

}