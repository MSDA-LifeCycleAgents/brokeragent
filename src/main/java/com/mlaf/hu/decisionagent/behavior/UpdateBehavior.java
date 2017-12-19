package com.mlaf.hu.decisionagent.behavior;

import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.decisionagent.InstructionSet;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;

public class UpdateBehavior extends CyclicBehaviour {
    private final DecisionAgent DA;

    public UpdateBehavior(DecisionAgent a) {
        this.DA = a;
    }

    @Override
    public void action() {
        for (InstructionSet instructionSetLocal : this.DA.sensorAgents.values()) {
            if (instructionSetLocal.nextDate().compareTo(new Date()) > 0) {
                instructionSetLocal.setActive(false);
            }
        }
        ACLMessage message = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE));
        InstructionSet is = this.DA.sensorAgents.get(message.getSender());
        is.setActive(true);

    }
}
