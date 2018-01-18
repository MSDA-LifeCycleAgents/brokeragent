package com.mlaf.hu.sensoragent;

import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.helpers.XmlParser;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.Messaging;
import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.sensoragent.behaviour.ReadSensorsBehaviour;
import com.mlaf.hu.sensoragent.behaviour.RegisterWithDABehaviour;
import com.mlaf.hu.sensoragent.behaviour.SendBufferBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.logging.Level;

public abstract class SensorAgent extends Agent {
    public static java.util.logging.Logger sensorAgentLogger = Logger.getLogger("SensorAgentLogger");
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private LinkedTransferQueue<SensorReading> sensorReadingQueue = new LinkedTransferQueue<>();
    private transient InstructionSet instructionSet;
    private ServiceDiscovery decisionAgentDiscovery;
    private boolean registered = false;

    public SensorAgent() {
        instructionSet = readInstructionSet();
        addBehaviour(new RegisterWithDABehaviour(this, 20000L));
        addBehaviour(new ReadSensorsBehaviour(this));
        decisionAgentDiscovery = new ServiceDiscovery(this, ServiceDiscovery.SD_DECISION_AGENT());
    }

    public void registerWithDA() {
        try {
            AID decisionAgent = decisionAgentDiscovery.getAID();
            ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
            message.setContent(this.getInstructionXML());
            message.addReceiver(decisionAgent);
            send(message);
        } catch (ServiceDiscoveryNotFoundException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not find DecisionAgent. Trying again in 20 seconds.\n" +
                    "Make sure the Decision Agent is active.");
        }

    }

    public List<Sensor> getSensors() {
        return new ArrayList<>(sensors);
    }

    protected abstract String getInstructionXML();

    public InstructionSet readInstructionSet() {
        try {
            return XmlParser.parseToObject(InstructionSet.class, getInstructionXML());
        } catch (ParseException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not parse the provided XML Instructionset, stopping agent.\nSee documentation for the correct InstructionSet.", e);
            this.doDelete();
            return null;
        }
    }

    protected void addSensor(Sensor newSensor) throws InvalidSensorException {
        for (Sensor s : sensors) {
            if (s.getSensorID().equals(newSensor.getSensorID())) {
                throw new InvalidSensorException("SensorImpl1 " + newSensor.getSensorID() + " is alreay registered");
            }
        }
        boolean foundInInstructionset = false;
        for (com.mlaf.hu.models.Sensor isSensor : instructionSet.getSensors().getSensors()) {
            if (isSensor.getId().equals(newSensor.getSensorID())) {
                foundInInstructionset = true;
                break;
            }
        }
        if (!foundInInstructionset) {
            throw new InvalidSensorException("SensorImpl1 " + newSensor.getSensorID() + " is not found in instructionset");
        }
        sensors.add(newSensor);
    }

    public void addSensorReadingToSendQueue(SensorReading sensorReading) {
        if (sensorReading.isEmpty()) {
            return;
        }
        sensorReadingQueue.add(sensorReading);
    }

    public void sendSensorReadings() throws ServiceDiscoveryNotFoundException, ServiceException{
        AID destination = null;
        destination = getDestination();
        SensorReading sensorReading = sensorReadingQueue.poll();
        if (sensorReading == null) {
            return;
        }
        String readingXml = null;
        try {
            readingXml = XmlParser.parseToXml(sensorReading);
        } catch (ParseException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not marshall the SensorImpl1 Reading.");
        }
        if (readingXml == null) {
            sensorAgentLogger.log(Level.SEVERE, "Got empty XML for sensor reading.");
            return;
        }
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(destination);
        msg.setLanguage("XML");
        msg.setOntology("MLAF-SensorImpl1-XML");
        msg.setContent(readingXml);
        send(msg);
        sensorAgentLogger.log(Level.INFO, String.format("New reading sent for sensor: %s", sensorReading.getSensors().getSensors().get(0).getId()));
    }

    private AID getDestination() throws ServiceDiscoveryNotFoundException, ServiceException {
        Messaging messaging = instructionSet.getMessaging();
        if (messaging.isDirectToDecisionAgent()) {
            return decisionAgentDiscovery.getAID();
        } else {
            TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            return topicHelper.createTopic(messaging.getTopic().getTopicName());
        }
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

}
