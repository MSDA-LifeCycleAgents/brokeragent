package com.mlaf.hu.sensoragent;

import com.mlaf.hu.helpers.XmlParser;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.Messaging;
import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.sensoragent.behavior.ReadSensorsBehavior;
import com.mlaf.hu.sensoragent.behavior.SendBufferBehavior;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.logging.Level;

public abstract class SensorAgent extends Agent {
    static protected java.util.logging.Logger sensorAgentLogger = Logger.getLogger("SensorAgentLogger");
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private LinkedTransferQueue<SensorReading> sensorReadingQueue = new LinkedTransferQueue<>();
    transient InstructionSet instructionSet;

    public SensorAgent() {
        addBehaviour(new ReadSensorsBehavior(this));
        addBehaviour(new SendBufferBehavior(this));
        instructionSet = readInstructionSet();
        registerWithDA();
    }

    public List<Sensor> getSensors() {
        return new ArrayList<>(sensors);
    }

    public void registerWithDA() {
        AID decisionAgent = getDecisionAgent();
        if (decisionAgent == null) {
            sensorAgentLogger.log(Level.SEVERE, "Could not find DecisionAgent");
            this.doDelete();
        }
        ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
        message.setContent(this.getInstructionXML());
        message.addReceiver(decisionAgent);
        send(message);
        //TODO Handle conversation to check wether the DA accepted it or not.

    }


    protected abstract String getInstructionXML();

    private InstructionSet readInstructionSet() {
        try {
            return XmlParser.parseToObject(InstructionSet.class, getInstructionXML());
        } catch (ParseException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not parse the provided XML Instructionset", e);
            //TODO maybe stop the program, we have yet to decide what to do when an unrecoverable error occurs
            return null;
        }
    }


    public void addSensor(Sensor newSensor) throws InvalidSensorException{
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
        if (sensorReading.isEmpty()) { return; }
        sensorReadingQueue.add(sensorReading);
    }

    public void sendSensorReadings() {
        AID destination = getDestination();
        if (destination == null) {
            return; // Destination is not found, maybe its offline, trying again in the future
        }

        SensorReading sensorReading = sensorReadingQueue.poll();
        String readingXml = null;
        try {
            readingXml = XmlParser.parseToXml(sensorReading);
        } catch (ParseException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not marshall ");
        }
        if (readingXml == null) {
            sensorAgentLogger.log(Level.SEVERE, "Got Empty XML for sensor reading");
            return;
        }
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(destination);
        msg.setLanguage("XML");
        msg.setOntology("MLAF-Sensor-XML");
        msg.setContent(readingXml);
        send(msg);
    }

    private AID getDestination() {
        Messaging messaging = instructionSet.getMessaging();
        if (messaging.isDirectToDecisionAgent()) {
            return getDecisionAgent();
        } else {
            return messaging.getTopic().getJadeTopic();
        }
    }


    private AID getDecisionAgent() {
        // Maybe rename to getDestination? Since sending to topic should also be possible.
        //TODO Replace with Service Discovery
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName("DECISION-AGENT");
        sd.setType("decision-agent");
        sd.addOntologies("MLAF-Sensor-XML");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (1==1) {System.out.println("Test");}
            if (result == null || result.length < 1) {
                return null;
            } else if (result.length > 1) {
                sensorAgentLogger.log(Level.WARNING, "Found multiple decisionagent, only using the first one");
                return result[0].getName();
                //TODO What do we want to do if multiple decisionagents are found?
            } else {
                return result[0].getName();
            }
        } catch (FIPAException e) {
            sensorAgentLogger.log(Level.SEVERE, "Could not get the Decision Agent", e);
            return null;
        }
    }

}
