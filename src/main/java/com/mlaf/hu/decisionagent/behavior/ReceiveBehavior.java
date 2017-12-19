package com.mlaf.hu.decisionagent.behavior;

import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.decisionagent.InstructionSet;
import com.mlaf.hu.decisionagent.SensorReading;
import com.mlaf.hu.decisionagent.representationmodels.Sensor;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveBehavior extends CyclicBehaviour {
    private final DecisionAgent DA;

    public ReceiveBehavior(DecisionAgent da) {
        this.DA = da;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        SensorReading sr  = DA.parseSensorReadingXml(message.getContent());
        InstructionSet is = DA.sensorAgents.get(message.getSender());
        for(Sensor inReading : sr.getSensors().getSensors()) {
            for (Sensor inInstructionSet: is.getSensors().getSensors()) {
                if (inReading.getId().equals(inInstructionSet.getId())) {
                    DA.handleSensorReading(inReading.getValue(), is, inInstructionSet);
                }
            }
        }
    }

}
