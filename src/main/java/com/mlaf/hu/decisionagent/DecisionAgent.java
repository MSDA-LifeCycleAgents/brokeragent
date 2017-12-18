package com.mlaf.hu.decisionagent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.util.Logger;

import java.util.HashMap;

public abstract class DecisionAgent extends Agent {
    private static final String SERVICE_NAME = "DECISION-AGENT";
    private static java.util.logging.Logger decisionAgentLogger = Logger.getLogger("DecisionAgentLogger");
    private HashMap<AID, InstructionSet> sensorAgents = new HashMap<>();

    public DecisionAgent() {}

    @Override
    protected void setup() {

    }

    public void registerAsService() {
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

    public void registerSensorAgent() {

    }

    public void unregisterSensorAgent() {

    }

    public InstructionSet parseInstructionXml(String xml) {

    }


}
