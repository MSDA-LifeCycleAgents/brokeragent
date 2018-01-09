package com.mlaf.hu.decisionagent.behavior;

import com.mlaf.hu.brokeragent.Topic;
import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.helpers.JadeServices;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.models.Measurement;
import com.mlaf.hu.models.Sensor;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.xml.bind.JAXB;
import java.io.StringWriter;


public class ReceiveBehavior extends CyclicBehaviour {
    private DecisionAgent DA;
    private AID brokeragent;

    public ReceiveBehavior(DecisionAgent da) {
        DA = da;
        this.brokeragent = JadeServices.getService("message-broker", this.DA);
    }

    @Override
    public void action() {
        ACLMessage directMessage = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (directMessage != null) {
            directMessaging(directMessage); // FIXME should it also respond with if it was understood or not?
        }
        topicMessaging();
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

    private void directMessaging(ACLMessage message) {
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
    }

    private void topicMessaging() {
        for (InstructionSet is : DA.sensorAgents.values()) {
            if (!is.getMessaging().isDirectToDecisionAgent()) {
                if (is.getMessaging().isRegisteredToTopic()) {
                    DA.send(requestFromTopic(this.brokeragent, is));
                } else {
                    DA.send(subscribeToTopic(this.brokeragent, is));
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
        Topic topic = new Topic(is.getMessaging().getTopic().getTopicName()); //FIXME shouldnt consist of daystokeep
        message.addReceiver(service);
        message.setContent(marshalTopic(topic));
        return message;
    }
    
    public String marshalTopic (Topic topic) {
        java.io.StringWriter marshalledTopic = new StringWriter();
        JAXB.marshal(topic, marshalledTopic);
        return marshalledTopic.toString();
    }
}
