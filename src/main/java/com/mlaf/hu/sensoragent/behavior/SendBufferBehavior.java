package com.mlaf.hu.sensoragent.behavior;

import com.mlaf.hu.sensoragent.Sensor;
import com.mlaf.hu.sensoragent.SensorAgent;
import com.mlaf.hu.sensoragent.SensorReading;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;

public class SendBufferBehavior extends TickerBehaviour {
    private final SensorAgent sensorAgent;

    public SendBufferBehavior(SensorAgent sa, long tickrate) {
        super(sa, tickrate);
        this.sensorAgent = sa;
    }


    @Override
    protected void onTick() {
        sensorAgent.sendSensorReadings();
    }
}
