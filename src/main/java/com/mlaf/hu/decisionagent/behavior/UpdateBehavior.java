package com.mlaf.hu.decisionagent.behavior;

import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.models.InstructionSet;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class UpdateBehavior extends CyclicBehaviour {
    private final DecisionAgent DA;

    public UpdateBehavior(DecisionAgent a) {
        this.DA = a;
    }

    @Override
    public void action() {
        for (InstructionSet localInstructionSet : this.DA.sensorAgents.values()) {
            if (ChronoUnit.SECONDS.between(localInstructionSet.getLastReceivedDataPackageAt(), LocalDateTime.now()) / localInstructionSet.getHighestIntervalFromSensors() >= localInstructionSet.getAmountOfMissedDataPackages()) {
                localInstructionSet.setActive(false);
            }
        }
        ACLMessage message = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE));
        if (message != null) {
            InstructionSet is = this.DA.sensorAgents.get(message.getSender());
            is.setActive(true);
        }

    }
}
