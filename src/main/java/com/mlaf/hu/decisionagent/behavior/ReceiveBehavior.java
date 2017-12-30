package com.mlaf.hu.decisionagent.behavior;

import com.mlaf.hu.brokeragent.Topic;
import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.decisionagent.InstructionSet;
import com.mlaf.hu.decisionagent.SensorReading;
import com.mlaf.hu.decisionagent.representationmodels.Measurement;
import com.mlaf.hu.decisionagent.representationmodels.Sensor;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.time.LocalDateTime;


public class ReceiveBehavior extends CyclicBehaviour {
    private DecisionAgent DA;
    private AID brokeragent;

    public ReceiveBehavior(DecisionAgent da) {
        DA = da;
        this.brokeragent = getService();
    }

    @Override
    public void action() {
        ACLMessage directMessage = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        directMessaging(directMessage);
        topicMessaging();
        ACLMessage isSubscribed = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        topicIsSubscribed(isSubscribed);
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
                    is.setLastReceivedDataPackageAt(LocalDateTime.now());
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

    private AID getService() { //FIXME nullpointer exception because DA.getDefaultDF() returns null. Why?
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("message-broker");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(DA, dfd);
            if (result.length > 0)
                return result[0].getName();
            else {
                DecisionAgent.decisionAgentLogger.log(Logger.SEVERE,
                        "The Broker Agent needs to be running in order to retrieve the service. \n" +
                        "Start the Broker Agent and restart the Decision Agent");
            }
        } catch (FIPAException fe) {
            DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, () -> String.format("Something went wrong trying to find the Broker Agent Service: %s", fe.getMessage()));
        }
        return null;
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
