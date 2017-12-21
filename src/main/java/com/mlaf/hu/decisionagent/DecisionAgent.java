package com.mlaf.hu.decisionagent;

import com.mlaf.hu.decisionagent.behavior.ReceiveBehavior;
import com.mlaf.hu.decisionagent.behavior.RegisterBehavior;
import com.mlaf.hu.decisionagent.behavior.UpdateBehavior;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.Sensor;
import com.mlaf.hu.models.SensorReading;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.util.Logger;

import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.util.HashMap;

public abstract class DecisionAgent extends Agent {
    private static final String SERVICE_NAME = "DECISION-AGENT";
    public static java.util.logging.Logger decisionAgentLogger = Logger.getLogger("DecisionAgentLogger");
    public HashMap<AID, InstructionSet> sensorAgents = new HashMap<>();

    public DecisionAgent() {
        super();
        registerAsService();
        addBehaviour(new RegisterBehavior(this));
        addBehaviour(new ReceiveBehavior(this));
        addBehaviour(new UpdateBehavior(this));
    }


    private void registerAsService() {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(this.getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(SERVICE_NAME);
            sd.setType("decision-agent");
            sd.addOntologies("decision-agent-ontology");
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            dfd.addServices(sd);
            DFService.register(this, dfd);
            decisionAgentLogger.log(Logger.INFO, "Registered the Decision Agent as a service to the DF.");
        } catch (FIPAException e) {
            decisionAgentLogger.log(Logger.SEVERE, () -> String.format("Registering as service failed: %s", e.getMessage()));
        }
    }

    public void registerSensorAgent(AID sensoragent, InstructionSet instructionset) {
        this.sensorAgents.put(sensoragent, instructionset);
    }

    public abstract void unregisterSensorAgent(AID sensoragent);

    public InstructionSet parseInstructionXml(String xml) {
        InstructionSet is = new InstructionSet();
        try {
            is = JAXB.unmarshal(new StringReader(xml), InstructionSet.class);
        } catch (Exception e) {
            decisionAgentLogger.log(Logger.SEVERE, String.format("Error parsing XML: %s", e.getMessage()));
        }
        return is;
    }

    public SensorReading parseSensorReadingXml (String xml) {
        SensorReading sr = new SensorReading();
        try {
            sr = JAXB.unmarshal(new StringReader(xml), SensorReading.class);
        } catch (Exception e) {
            decisionAgentLogger.log(Logger.SEVERE, String.format("Error parsing XML: %s", e.getMessage()));
        }
        return sr;
    }

    public void handleSensorReading(int value, InstructionSet is, Sensor sensor) {
        sensor.getReadings().add(value);
        storeReading(value);
    }

    public abstract void storeReading(int value);


}
