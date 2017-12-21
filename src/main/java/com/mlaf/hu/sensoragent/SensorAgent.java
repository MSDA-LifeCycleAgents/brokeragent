package com.mlaf.hu.sensoragent;

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
import java.util.logging.Level;

public abstract class SensorAgent extends Agent {
    static java.util.logging.Logger sensorAgentLogger = Logger.getLogger("SensorAgentLogger");
    private ArrayList<Sensor> sensors = new ArrayList<>();
    protected ArrayList<SensorReading> sensorReadingBuffer = new ArrayList<>();
    boolean directToDecisionAgent = true; //TODO Read this from Instruction XML Maybe use thijs code?

    public SensorAgent(int sensorRefreshRateMs) {
        addBehaviour(new ReadSensorsBehavior(this, sensorRefreshRateMs));
        addBehaviour(new SendBufferBehavior(this, sensorRefreshRateMs));
    }

    public List<Sensor> getSensors() {
        return new ArrayList<>(sensors);
    }


    public abstract String getInstructionXML();

    public boolean addSensor(Sensor newSensor) {
        for (Sensor s : sensors) {
            if (s.getSensorID().equals(newSensor.getSensorID())) {
                return false;
            }
        }
        sensors.add(newSensor);
        return true;
    }

    public void addSensorReadingToSendBuffer(SensorReading sensorReading) {
        sensorReadingBuffer.add(sensorReading);
    }

    public void sendSensorReadings() {
        AID destination = getDestination();
        if (destination == null) {
            return; // Destination is not found, maybe its offline, trying again in the future
        }
        for (SensorReading sensorReading: sensorReadingBuffer) {
            String readingXML = sensorReading.toXML();
            if (readingXML == null) {
                sensorAgentLogger.log(Level.WARNING, "XML for Sensorreading is empty, skipping");
                continue;
            }
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(destination);
            msg.setLanguage("XML");
            msg.setOntology("MLAF-Sensor-XML");
            msg.setContent(readingXML);
            send(msg);
        }
        sensorReadingBuffer.clear();
    }

    private AID getDestination() {
        if (directToDecisionAgent) {
            return getDecisionAgent();
        } else {
            return getSensorReadingsTopic();
        }

    }

    private AID getSensorReadingsTopic() {
        return null; // TODO implement function to get topic from
    }

    private AID getDecisionAgent() {
        // Maybe rename to getDestination? Since sending to topic should also be possible.
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("sensor-reading");
        sd.addOntologies("MLAF-Sensor-XML");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length < 1) {
                return null;
            } else if (result.length > 1) {
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
