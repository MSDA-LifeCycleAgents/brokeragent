package com.mlaf.hu.decisionagent.behaviour;

import com.mlaf.hu.brokeragent.BrokerAgent;
import com.mlaf.hu.models.Topic;
import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.helpers.exceptions.SensorNotFoundException;
import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.models.Measurement;
import com.mlaf.hu.models.Sensor;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * This behaviour will according to the instructionset either wait to receive direct INFORM messages with it's data-package as content
 * or it will ask the Broker Agent to hand him another datapackage. This second option will result in the Broker Agent sending an INFORM ACLMessage with
 * a data-package inside, just like with handleDirectMessage. In the end all the data-packages are received by the method
 * handleDirectMessage.
 */

public class ReceiveBehaviour extends CyclicBehaviour {
    private DecisionAgent DA;
    private ServiceDiscovery serviceDiscovery;
    private LocalDateTime continueAfter = LocalDateTime.now();

    public ReceiveBehaviour(DecisionAgent da) {
        this.DA = da;
        ServiceDescription ba = BrokerAgent.createServiceDescription();
        this.serviceDiscovery = new ServiceDiscovery(this.DA, ba);
    }

    @Override
    public void action() {
        ACLMessage directMessage = myAgent.receive(MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchOntology("sensor-agent-reading")
        ));
        if (directMessage != null) {
            ACLMessage response = handleDirectMessage(directMessage);
            this.DA.send(response);
        }
        if (LocalDateTime.now().isAfter(this.continueAfter)) {
            try {
                handleTopicMessaging();
            } catch (ServiceDiscoveryNotFoundException e) {
                DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, e.getMessage());
                this.continueAfter = LocalDateTime.now().plusSeconds(20);
            }
        }
        ACLMessage isSubscribed = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        if (isSubscribed != null && isSubscribed.getUserDefinedParameter("name") != null) {
            topicIsSubscribed(isSubscribed);
        }
    }

    private void topicIsSubscribed(ACLMessage isSubscribed) {
        String topicName = isSubscribed.getUserDefinedParameter("name");
        for (InstructionSet is : this.DA.sensorAgents.values()) {
            if (is.getMessaging().getTopic().getTopicName().equals(topicName)) {
                is.getMessaging().setRegisteredToTopic(true);
            }
        }
    }

    private ACLMessage handleDirectMessage(ACLMessage message) {
        ACLMessage response = message.createReply();
        response.setPerformative(ACLMessage.CONFIRM);
        if (!this.DA.sensorAgentExists(message.getSender())) {
            response.setPerformative(ACLMessage.DISCONFIRM);
            response.setOntology("sensor-agent-register");
            response.setContent("Not registered yet.");
            return response;
        }
        try {
            SensorReading sr = this.DA.parseSensorReadingXml(message.getContent());
            InstructionSet is = this.DA.sensorAgents.get(message.getSender());
            for (Sensor inReading : sr.getSensors().getSensors()) {
                Sensor inInstructionSet = is.getSensor(inReading.getId());
                for (Measurement ms : inReading.getMeasurements().getMeasurements()) {
                    DA.handleSensorReading(ms.getValue(), is, inInstructionSet, ms.getId());
                }
            }
        } catch (ParseException e) {
            DecisionAgent.decisionAgentLogger.log(Logger.WARNING, e.getMessage());
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Composition: wrong, check documentation.");
        } catch (SensorNotFoundException e) {
            DecisionAgent.decisionAgentLogger.log(Logger.WARNING,"InstructionSet misses sensor from sensor reading: " + e.getMessage());
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("InstructionSet misses sensor from sensor reading: " + e.getMessage());
        }
        return response;
    }

    private void handleTopicMessaging() throws ServiceDiscoveryNotFoundException {
        for (InstructionSet is : this.DA.sensorAgents.values()) {
            if (!is.getMessaging().isDirectToDecisionAgent()) {
                if (is.getMessaging().isRegisteredToTopic()) {
                    this.DA.send(requestFromTopic(this.serviceDiscovery.getAID(), is));
                }
                if (LocalDateTime.now().isAfter(this.continueAfter)) {
                    this.DA.send(subscribeToTopic(this.serviceDiscovery.getAID(), is));
                    this.continueAfter = LocalDateTime.now().plusSeconds(30);
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
