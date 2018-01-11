package com.mlaf.hu.decisionagent.behaviour;

import com.mlaf.hu.brokeragent.Topic;
import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.models.Measurement;
import com.mlaf.hu.models.Sensor;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import javax.xml.bind.JAXB;
import java.io.StringWriter;

/**
 *  This behaviour will according to the instructionset either wait to receive direct INFORM messages with it's data-package as content
 *  or it will ask the Broker Agent to hand him another datapackage. This second option will result in the Broker Agent sending an INFORM ACLMessage with
 *  a data-package inside, just like with handleDirectMessage. In the end all the data-packages are received by the method
 *  handleDirectMessage.
 */

public class ReceiveBehaviour extends CyclicBehaviour {
    private DecisionAgent DA;
    private AID brokerAgent;

    public ReceiveBehaviour(DecisionAgent da) {
        DA = da;
    }

    @Override
    public void action() {
        ACLMessage directMessage = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (directMessage != null) {
            ACLMessage response = handleDirectMessage(directMessage);
            this.DA.send(response);
        }
        handleTopicMessaging();
        ACLMessage isSubscribed = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        if (isSubscribed != null) {
            topicIsSubscribed(isSubscribed);
        }
    }

    private void topicIsSubscribed(ACLMessage isSubscribed) {
        String topicName = isSubscribed.getUserDefinedParameter("name");
        for (InstructionSet is : DA.sensorAgents.values()) {
            if (is.getMessaging().getTopic().getTopicName().equals(topicName)) {
                is.getMessaging().setRegisteredToTopic(true);
            }
        }
    }

    private ACLMessage handleDirectMessage(ACLMessage message) {
        ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
        try {
            SensorReading sr = DA.parseSensorReadingXml(message.getContent());
            InstructionSet is = DA.sensorAgents.get(message.getSender());
            for (Sensor inReading : sr.getSensors().getSensors()) {
                for (Sensor inInstructionSet : is.getSensors().getSensors()) {
                    if (inReading.getId().equals(inInstructionSet.getId())) {
                        for (Measurement ms : inReading.getMeasurements().getMeasurements()) {
                            DA.handleSensorReading(ms.getValue(), is, inInstructionSet, ms.getId());
                        }
                    }
                }
            }
            response.setContent("Composition: ok");
        } catch (ParseException e) {
            DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, e.getMessage());
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Composition: wrong, check documentation.");
        }
        return response;
    }

    private void handleTopicMessaging() {
        for (InstructionSet is : DA.sensorAgents.values()) {
            if (!is.getMessaging().isDirectToDecisionAgent()) {
                if (is.getMessaging().isRegisteredToTopic()) {
                    DA.send(requestFromTopic(this.brokerAgent, is));
                } else {
                    DA.send(subscribeToTopic(this.brokerAgent, is));
                }
            }
        }
    }

    private ACLMessage subscribeToTopic(AID service, InstructionSet is) {
        ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
        Topic topic = is.getMessaging().getTopic();
        message.addReceiver(service);
        message.setContent(marshalTopic(topic));
        return message;
    }

    public ACLMessage requestFromTopic(AID service, InstructionSet is) {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        Topic topic = new Topic(is.getMessaging().getTopic().getTopicName());
        message.addReceiver(service);
        message.setContent(marshalTopic(topic));
        return message;
    }

    public String marshalTopic(Topic topic) {
        java.io.StringWriter marshalledTopic = new StringWriter();
        JAXB.marshal(topic, marshalledTopic);
        return marshalledTopic.toString();
    }
}
