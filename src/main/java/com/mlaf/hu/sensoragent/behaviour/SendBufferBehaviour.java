package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.behaviours.CyclicBehaviour;


public class SendBufferBehaviour extends CyclicBehaviour {
    private final SensorAgent sensorAgent;

    public SendBufferBehaviour(SensorAgent sa) {
        super(sa);
        this.sensorAgent = sa;
    }

    @Override
    public void action() {
        sensorAgent.sendSensorReadings();
    }
}
