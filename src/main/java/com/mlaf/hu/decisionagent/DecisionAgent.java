package com.mlaf.hu.decisionagent;

import com.mlaf.hu.decisionagent.behavior.ReceiveBehavior;
import com.mlaf.hu.decisionagent.behavior.RegisterBehavior;
import com.mlaf.hu.decisionagent.behavior.UpdateBehavior;
import com.mlaf.hu.models.*;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.util.Logger;

import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            registerAsService();
            addBehaviour(new RegisterBehavior(this));
            addBehaviour(new ReceiveBehavior(this));
            addBehaviour(new UpdateBehavior(this));
        } catch (Exception e) {
            DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, "Could not initialize BrokerAgent", e);
            System.exit(1);
        }
    }

    private void registerAsService() {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(this.getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(SERVICE_NAME);
            sd.setType("decision-agent");
            sd.addOntologies("decision-agent-ontology");
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            dfd.addServices(sd);
            DFService.register(this, dfd);
            decisionAgentLogger.log(Logger.INFO, "Registered the Decision Agent as a service to the DF.");
        } catch (FIPAException e) {
            decisionAgentLogger.log(Logger.SEVERE, () -> String.format("Registering as service failed: %s", e.getMessage()));
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

    public void executePlan(Plan plan) {
        Class communcationHelper = null;
        try {
            communcationHelper = Class.forName(String.format("communication.%s", plan.getVia()));
            Object invoker = (Object) communcationHelper.newInstance();
            Void send = (Void) genericInvokMethod(invoker, "send", 2, plan.getMessage(), plan.getTo());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static Object genericInvokMethod(Object obj, String methodName,
                                             int paramCount, Object... params) {
        Method method;
        Object requiredObj = null;
        Object[] parameters = new Object[paramCount];
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            parameters[i] = params[i];
            classArray[i] = params[i].getClass();
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return requiredObj;
    }
}
