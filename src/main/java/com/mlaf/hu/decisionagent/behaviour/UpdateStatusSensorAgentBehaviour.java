package com.mlaf.hu.decisionagent.behaviour;

import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.models.InstructionSet;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * This behaviour will continuously check if the SensorImpl1 Agents are still alive and kicking. It will do this by checking for every InstructionSet
 * if the difference in the lastReceivedDataPackageAt and the time at that moment in seconds divided by the highest interval (in seconds) of all Sensors in the InstructionSet
 * is equal or higher than amountOfMissedDataPackages. If so the SensorImpl1 will be set to inactive.
 */

public class UpdateStatusSensorAgentBehaviour extends TickerBehaviour {
    private final DecisionAgent DA;

    public UpdateStatusSensorAgentBehaviour(DecisionAgent a, long frequency) {
        super(a, frequency);
        this.DA = a;
    }

    @Override
    protected void onTick() {
        checkForInactivity();
        ACLMessage message = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE));
        updateToActive(message);
    }

    private void checkForInactivity() {
        for (Map.Entry<AID, InstructionSet> sensorAgent : this.DA.sensorAgents.entrySet()) {
            InstructionSet localInstructionSet = sensorAgent.getValue();
            if (localInstructionSet.getLastReceivedDataPackageAt() != null) {
                long passedTime = ChronoUnit.SECONDS.between(localInstructionSet.getLastReceivedDataPackageAt(), LocalDateTime.now());
                int missedDataPackages = (int) (passedTime / localInstructionSet.getHighestIntervalFromSensors());
                if (missedDataPackages >= localInstructionSet.getAmountOfMissedDataPackages()) {
                    this.DA.unregisterSensorAgent(sensorAgent.getKey());
                }
            } else if (localInstructionSet.getRegisteredAt() != null) {
                long passedTime = ChronoUnit.SECONDS.between(localInstructionSet.getRegisteredAt(), LocalDateTime.now());
                int missedDataPackages = (int) (passedTime / localInstructionSet.getHighestIntervalFromSensors());
                if (missedDataPackages >= localInstructionSet.getAmountOfMissedDataPackages()) {
                    this.DA.unregisterSensorAgent(sensorAgent.getKey());
                }
            }
        }
    }

    private void updateToActive(ACLMessage message) {
        if (message != null) {
            InstructionSet is = this.DA.sensorAgents.get(message.getSender());
            is.setActive();
        }
    }

}
