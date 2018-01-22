package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.ServiceException;
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
        try {
            if(LocalDateTime.now().isAfter(continueAfter)) {
                sensorAgent.sendSensorReadings();
            }
        } catch (ServiceDiscoveryNotFoundException | ServiceException e) {
            SensorAgent.sensorAgentLogger.log(Logger.SEVERE, "Stop sending and waiting 20 seconds: " + e.getMessage());
            continueAfter = LocalDateTime.now().plusSeconds(20);
        }

    }
}
