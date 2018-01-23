package com.mlaf.hu.implementationexample.decisionagent;

import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.Measurement;
import com.mlaf.hu.models.Plan;
import com.mlaf.hu.models.Sensor;
import jade.core.AID;
import com.mlaf.hu.decisionagent.DecisionAgent;
import jade.util.Logger;

public class DecisionAgentImpl extends DecisionAgent {

    @Override
    public void unregisterSensorAgentCallback(AID sensoragent) {

    }

    @Override
    public void storeReading(double value, InstructionSet is, Sensor sensor, String measurementId) {

    }

    @Override
    public void executePlanCallback(Plan plan) {

    }

    @Override
    protected void executeSensorReadingWarning(Sensor sensor, Measurement measurement, double reading) {
        DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, String.format("Measurement %s from Sensor %s has exceeded the min or max value: %s", measurement.getId(), sensor.getId(), reading));
    }

}
