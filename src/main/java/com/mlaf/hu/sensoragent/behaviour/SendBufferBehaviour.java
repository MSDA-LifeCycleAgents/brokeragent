package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.util.Logger;


public class SendBufferBehaviour extends CyclicBehaviour {
    private final SensorAgent sensorAgent;

    public SendBufferBehaviour(SensorAgent sa) {
        super(sa);
        this.sensorAgent = sa;
    }

    @Override
    public void action() {
        try {
            sensorAgent.sendSensorReadings();
        } catch (ServiceDiscoveryNotFoundException | ServiceException e) {
            SensorAgent.sensorAgentLogger.log(Logger.SEVERE, "Stop sending and waiting 20 seconds: " + e.getMessage());
            try {
                Thread.sleep(20000L);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }
}
