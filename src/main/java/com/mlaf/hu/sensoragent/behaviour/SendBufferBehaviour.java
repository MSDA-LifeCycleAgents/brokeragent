package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.util.Logger;

import java.time.LocalDateTime;


public class SendBufferBehaviour extends CyclicBehaviour {
    private final SensorAgent sensorAgent;
    private ServiceDiscovery sd;
    private LocalDateTime continueAfter = LocalDateTime.now();

    public SendBufferBehaviour(SensorAgent sa) {
        super(sa);
        this.sensorAgent = sa;
        if (this.sensorAgent.getInstructionSet().getMessaging().isDirectToDecisionAgent()) {
            this.sd = new ServiceDiscovery(this.sensorAgent, ServiceDiscovery.SD_DECISION_AGENT());
        } else {
            this.sd = new ServiceDiscovery(this.sensorAgent, ServiceDiscovery.SD_BROKER_AGENT());
        }
    }

    @Override
    public void action() {
        if (LocalDateTime.now().isAfter(continueAfter)) {
            try {
                if (this.sd.getAID() != null) {
                    sensorAgent.sendSensorReadings();
                }
            } catch (ServiceDiscoveryNotFoundException e) {
                SensorAgent.sensorAgentLogger.log(Logger.SEVERE, "Stop sending and waiting 20 seconds. Destination unclear.");
                continueAfter = LocalDateTime.now().plusSeconds(20);
            }
        }
    }
}
