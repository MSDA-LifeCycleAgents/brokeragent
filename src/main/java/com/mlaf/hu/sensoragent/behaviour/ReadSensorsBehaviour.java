package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.models.SensorReading;
import com.mlaf.hu.sensoragent.Sensor;
import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.behaviours.CyclicBehaviour;


public class ReadSensorsBehaviour extends CyclicBehaviour {
    private final SensorAgent sensorAgent;

    public ReadSensorsBehaviour(SensorAgent sa) {
        super(sa);
        this.sensorAgent = sa;
    }

    @Override
    public void action() {
        SensorReading sensorReading = new SensorReading();
        for (Sensor sensor: sensorAgent.getSensors()) {
            if (sensor.mustBeRead()) {
                com.mlaf.hu.models.Sensor dataSensor = sensor.toDataSensor();
                sensorReading.addSensor(dataSensor);
                sensor.markRead();
            }
        }
        if (sensorReading.isEmpty()) {
            return;
        }

        sensorAgent.addSensorReadingToSendQueue(sensorReading);
    }
}
