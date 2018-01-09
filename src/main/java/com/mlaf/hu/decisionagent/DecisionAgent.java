package com.mlaf.hu.decisionagent;

import com.mlaf.hu.decisionagent.behavior.ReceiveBehavior;
import com.mlaf.hu.decisionagent.behavior.RegisterBehavior;
import com.mlaf.hu.decisionagent.behavior.UpdateBehavior;
import com.mlaf.hu.helpers.JadeServices;
import com.mlaf.hu.models.*;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.util.Logger;

import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class DecisionAgent extends Agent {
    private static final String SERVICE_NAME = "DECISION-AGENT";
    private static final int MAX_READINGS = 100;
    public static java.util.logging.Logger decisionAgentLogger = Logger.getLogger("DecisionAgentLogger");
    public HashMap<AID, InstructionSet> sensorAgents = new HashMap<>();

    public DecisionAgent() {
        super();
    }

    @Override
    protected void setup() {
        try {
            JadeServices.registerAsService(SERVICE_NAME, "decision-agent", null, null, this);
            addBehaviour(new RegisterBehavior(this));
            addBehaviour(new ReceiveBehavior(this));
            addBehaviour(new UpdateBehavior(this));
        } catch (Exception e) {
            DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, "Could not initialize BrokerAgent", e);
            System.exit(1);
        }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ignore) {
        }
    }

    public void registerSensorAgent(AID sensoragent, InstructionSet instructionset) {
        this.sensorAgents.put(sensoragent, instructionset);
    }

    public abstract void unregisterSensorAgent(AID sensoragent);

    public InstructionSet parseInstructionXml(String xml) {
        InstructionSet is = new InstructionSet();
        try {
            is = JAXB.unmarshal(new StringReader(xml), InstructionSet.class);
        } catch (Exception e) {
            decisionAgentLogger.log(Logger.SEVERE, String.format("Error parsing XML: %s", e.getMessage()));
        }
        return is;
    }

    public SensorReading parseSensorReadingXml(String xml) {
        SensorReading sr = new SensorReading();
        try {
            sr = JAXB.unmarshal(new StringReader(xml), SensorReading.class);
        } catch (Exception e) {
            decisionAgentLogger.log(Logger.SEVERE, String.format("Error parsing XML: %s", e.getMessage()));
        }
        return sr;
    }

    public void handleSensorReading(double value, InstructionSet is, Sensor sensor, String measurementId) {
        try {
            Measurement measurement = sensor.getMeasurements().getMeasurement(measurementId);
            ArrayList<Double> readings = measurement.getReadings();
            readings.add(value);
            is.setLastReceivedDataPackageAt(LocalDateTime.now());
            if (readings.size() > MAX_READINGS) {
                readings.remove(readings.size() - 1);
            }
        } catch (NullPointerException npe) {
            decisionAgentLogger.log(Logger.SEVERE, String.format("No measurement found by that ID: %s", measurementId));
        }
        storeReading(value);
    }

    public abstract void storeReading(double value);

    public void decide(double reading, Measurement measurement) {
        for (Plan plan : measurement.getPlans().getPlans()) {
            if ((measurement.getMax() * plan.getAbove() < reading) || (reading < measurement.getMax() * plan.getBelow())) {
                executePlan(plan);
            }
        }
    }

    public abstract void executePlan(Plan plan);

}
