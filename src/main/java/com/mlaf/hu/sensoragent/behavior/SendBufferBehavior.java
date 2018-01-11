package com.mlaf.hu.sensoragent.behavior;

import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.behaviours.CyclicBehaviour;


public class SendBufferBehavior extends CyclicBehaviour {
    private final SensorAgent sensorAgent;

    public SendBufferBehavior(SensorAgent sa) {
        super(sa);
        this.sensorAgent = sa;
    }

    @Override
    public void action() {
        sensorAgent.sendSensorReadings();
    }
}
