package com.mlaf.hu.sensoragent;

import com.mlaf.hu.helpers.Configuration;
import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.helpers.XmlParser;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import com.mlaf.hu.loggeragent.LoggerAgentLogHandler;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.Messaging;
import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.models.Topic;
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
    private static Configuration config = Configuration.getInstance();
    private static final boolean LOGGER_HANDLER = Boolean.parseBoolean(config.getProperty("sensoragent.logger_handler"));
    public static java.util.logging.Logger sensorAgentLogger = Logger.getLogger("SensorAgentLogger");
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private LinkedTransferQueue<SensorReading> sensorReadingQueue = new LinkedTransferQueue<>();
    private transient InstructionSet instructionSet;
    private ServiceDiscovery decisionAgentDiscovery;
    private boolean registered = false;
    private AID destination;

    public SensorAgent() {
        if (LOGGER_HANDLER) {
            sensorAgentLogger.addHandler(new LoggerAgentLogHandler(this, 60));
        }
        instructionSet = readInstructionSet();
        addBehaviour(new RegisterWithDABehaviour(this));
        addBehaviour(new ReadSensorsBehaviour(this));
        decisionAgentDiscovery = new ServiceDiscovery(this, ServiceDiscovery.SD_DECISION_AGENT());
    }

    /**
     * This function should get the Instruction Set from where ever it is stored.
     */
    protected abstract String getInstructionXML();

    /**
     * When the Decision Agent doesn't accept the Instruction Set XML send in the RegisterWithDABehaviour.
     * Suggestion for this method implementation: Stop the Sensor Agent to fix the Instruction Set XML according
     * to the documentation and start the Sensor Agent back up.
     */
    public abstract void onReceivingRefuseRegistration();

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

    private InstructionSet readInstructionSet() {
        String instrctionSetStr = null;
        try {
            instrctionSetStr = getInstructionXML();
            return XmlParser.parseToObject(InstructionSet.class, instrctionSetStr);
        } catch (ParseException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not parse the provided XML Instructionset, " +
                    "stopping agent.\nSee documentation for the correct InstructionSet." +
                    "\nInstructionSet: " + instrctionSetStr, e);
            this.doDelete();
            return null;
        }
    }

    protected void addSensor(Sensor newSensor) throws InvalidSensorException {
        for (Sensor s : sensors) {
            if (s.getSensorID().equals(newSensor.getSensorID())) {
                throw new InvalidSensorException("Sensor " + newSensor.getSensorID() + " is alreay registered");
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
            throw new InvalidSensorException("Sensor " + newSensor.getSensorID() + " is not found in instructionset");
        }
        sensors.add(newSensor);
    }

    public void addSensorReadingToSendQueue(SensorReading sensorReading) {
        if (sensorReading.isEmpty()) {
            return;
        }
        sensorReadingQueue.add(sensorReading);
    }

    public void sendSensorReadings(){
        SensorReading sensorReading = sensorReadingQueue.poll();
        if (sensorReading == null) {
            return;
        }
        String readingXml = null;
        try {
            readingXml = XmlParser.parseToXml(sensorReading);
        } catch (ParseException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not marshall the Sensor Reading.");
        }
        if (readingXml == null) {
            sensorAgentLogger.log(Level.SEVERE, "Got empty XML for sensor reading.");
            return;
        }
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(destination);
        msg.setLanguage("XML");
        msg.setOntology("sensor-agent-reading");
        msg.setContent(readingXml);
        send(msg);
        sensorAgentLogger.log(Level.INFO, String.format("New reading sent to %s for sensor: %s", destination, sensorReading.getSensors().getSensors().get(0).getId()));
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }


    public void setDestination(AID destination) {
        this.destination = destination;
    }

    public AID getDestination() {
        return this.destination;
    }
}
