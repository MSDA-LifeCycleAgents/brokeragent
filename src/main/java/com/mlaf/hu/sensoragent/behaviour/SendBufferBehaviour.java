package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.util.Logger;

import java.time.LocalDateTime;


public class SendBufferBehaviour extends CyclicBehaviour {
    private final SensorAgent sensorAgent;
    private LocalDateTime continueAfter = LocalDateTime.now();

    public SendBufferBehaviour(SensorAgent sa) {
        super(sa);
        this.sensorAgent = sa;
    }

    @Override
    public void action() {
        if (LocalDateTime.now().isAfter(continueAfter) && sensorAgent.getDestination() != null) {
            sensorAgent.sendSensorReadings();
        } else if (sensorAgent.getDestination() == null) {
            SensorAgent.sensorAgentLogger.log(Logger.SEVERE, "Stop sending and waiting 20 seconds. Destination unclear.");
            continueAfter = LocalDateTime.now().plusSeconds(20);
        }

    }
}
