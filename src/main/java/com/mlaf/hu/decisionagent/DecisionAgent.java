package com.mlaf.hu.decisionagent;

import com.mlaf.hu.decisionagent.behaviour.ReceiveBehaviour;
import com.mlaf.hu.decisionagent.behaviour.RegisterSensorAgentBehaviour;
import com.mlaf.hu.decisionagent.behaviour.SaveToDiskBehaviour;
import com.mlaf.hu.decisionagent.behaviour.UpdateStatusSensorAgentBehaviour;
import com.mlaf.hu.helpers.Configuration;
import com.mlaf.hu.helpers.DFServices;
import com.mlaf.hu.helpers.XmlParser;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.models.*;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class DecisionAgent extends Agent {
    private static Configuration config = Configuration.getInstance();
    private static final String SERVICE_NAME = config.getProperty("decisionagent.service_name");
    private static final String STORAGE_BASEPATH = config.getProperty("decisionagent.storage_basepath");
    public static final long STORE_INTERVAL_IN_MS = Long.parseLong(config.getProperty("decisionagent.store_interval_in_ms"));
    private static final String STORAGE_FILENAME = config.getProperty("decisionagent.storage_filename");
    private static final boolean STORE_SENSOR_AGENTS_ON_DISK = Boolean.parseBoolean(config.getProperty("decisionagent.store_sensor_agents_on_disk"));
    public static java.util.logging.Logger decisionAgentLogger = Logger.getLogger("DecisionAgentLogger");
    public HashMap<AID, InstructionSet> sensorAgents = new HashMap<>();

    public DecisionAgent() {
        super();
    }

    @Override
    protected void setup() {
        if(STORE_SENSOR_AGENTS_ON_DISK) {
            boolean success = createDirectoryStructure();
            if (new File(STORAGE_BASEPATH).exists() || success) {
                loadSensorAgents();
                addBehaviour(new SaveToDiskBehaviour(this));
            }
        }
        DFServices.registerAsService(createServiceDescription(), this);
        addBehaviour(new RegisterSensorAgentBehaviour(this));
        addBehaviour(new ReceiveBehaviour(this));
        addBehaviour(new UpdateStatusSensorAgentBehaviour(this, 5000L));
    }

    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName(SERVICE_NAME);
        sd.setType("decision-agent");
        return sd;
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ignore) {
        }
    }

    public boolean sensorAgentExists(AID sensorAgent) {
        return sensorAgents.get(sensorAgent) != null;
    }

    public void registerSensorAgent(AID sensoragent, InstructionSet instructionset) {
        if (!sensorAgentExists(sensoragent)) {
            instructionset.setRegisteredAt(LocalDateTime.now());
            this.sensorAgents.put(sensoragent, instructionset);
            DecisionAgent.decisionAgentLogger.log(Logger.INFO, "New SensorImpl1 Agent added: " + sensoragent);
        }
    }

    public void unregisterSensorAgent(AID sensoragent) {
        this.sensorAgents.remove(sensoragent);
        unregisterSensorAgentCallback(sensoragent);
    }

    public abstract void unregisterSensorAgentCallback(AID sensoragent);

    public InstructionSet parseInstructionXml(String xml) throws ParseException {
        return XmlParser.parseToObject(InstructionSet.class, xml);
    }

    public SensorReading parseSensorReadingXml(String xml) throws ParseException {
        return XmlParser.parseToObject(SensorReading.class, xml);
    }

    public void handleSensorReading(double value, InstructionSet is, Sensor sensor, String measurementId) {
        try {
            Measurement measurement = sensor.getMeasurements().getMeasurement(measurementId);
            CircularFifoQueue<Double> readings = measurement.getReadings();
            readings.add(value);
            is.setLastReceivedDataPackageAt(LocalDateTime.now());
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

    private void executePlan(Plan plan) {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent(plan.getMessage());
        message.addReceiver(DFServices.getService(plan.getVia(), this));
        message.addUserDefinedParameter("to", plan.getTo());
        this.send(message);
        executePlanCallback(plan);
    }

    public abstract void executePlanCallback(Plan plan);

     public void storeSensorAgents() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"))) {
            decisionAgentLogger.log(Logger.INFO, String.format("Writing the following Sensor Agents w. Instruction Sets to disk: \n%s", HashMapToString()));
            oos.writeObject(this.sensorAgents);
            decisionAgentLogger.log(Logger.INFO, String.format("Written all Sensor Agents w. Instruction Sets to: %s", STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"));
        } catch (IOException e) {
            e.printStackTrace();
            decisionAgentLogger.log(Logger.SEVERE, "Could not write Sensor Agents w. Instruction Sets to disk.");
        }
    }

    private void loadSensorAgents() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"))) {
            this.sensorAgents = (HashMap) ois.readObject();
            decisionAgentLogger.log(Logger.INFO, String.format("Found serialized Sensor Agents w. Instruction Sets: \n%s", HashMapToString()));
        } catch (FileNotFoundException e) {
            decisionAgentLogger.log(Logger.INFO, String.format("Could not find serialized Sensor Agents w. Instruction Sets on disk: %s. Starting fresh.", e.getMessage()));
        } catch (IOException | ClassNotFoundException e) {
            decisionAgentLogger.log(Logger.INFO, String.format("Could not load file, IO Error: %s. Starting fresh.", e.getMessage()));
        }
    }

    private String HashMapToString() {
        StringBuilder toString = new StringBuilder();
        for (Map.Entry<AID, InstructionSet> entry : this.sensorAgents.entrySet()) {
            toString.append(entry.getKey()).append(" \n");
        }
        return toString.toString();
    }

    private static boolean createDirectoryStructure() {
        return (new File(STORAGE_BASEPATH).mkdirs()); // Return success
    }


}
