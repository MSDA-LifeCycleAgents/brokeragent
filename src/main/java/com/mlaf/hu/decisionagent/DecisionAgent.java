package com.mlaf.hu.decisionagent;

import com.mlaf.hu.decisionagent.behaviour.ReceiveBehaviour;
import com.mlaf.hu.decisionagent.behaviour.RegisterSensorAgentBehaviour;
import com.mlaf.hu.decisionagent.behaviour.SaveToDiskBehaviour;
import com.mlaf.hu.decisionagent.behaviour.UpdateStatusSensorAgentBehaviour;
import com.mlaf.hu.helpers.Configuration;
import com.mlaf.hu.helpers.DFServices;
import com.mlaf.hu.helpers.XmlParser;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.loggeragent.LoggerAgentLogHandler;
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

public class DecisionAgent extends Agent {
    private static Configuration config = Configuration.getInstance();
    private static final String SERVICE_NAME = config.getProperty("decisionagent.service_name");
    private static final String STORAGE_BASEPATH = config.getProperty("decisionagent.storage_basepath");
    public static final long STORE_INTERVAL_IN_MS = Long.parseLong(config.getProperty("decisionagent.store_interval_in_ms"));
    private static final String STORAGE_FILENAME = config.getProperty("decisionagent.storage_filename");
    private static final boolean STORE_SENSOR_AGENTS_ON_DISK = Boolean.parseBoolean(config.getProperty("decisionagent.store_sensor_agents_on_disk"));
    private static final boolean LOGGER_HANDLER = Boolean.parseBoolean(config.getProperty("decisionagent.logger_handler"));
    public static java.util.logging.Logger decisionAgentLogger = Logger.getLogger("DecisionAgentLogger");
    public HashMap<AID, InstructionSet> sensorAgents = new HashMap<>();
    private boolean isUpdatingStatus;

    public DecisionAgent() {
        super();
    }

    @Override
    protected void setup() {
        if (STORE_SENSOR_AGENTS_ON_DISK) {
            boolean success = createDirectoryStructure();
            if (new File(STORAGE_BASEPATH).exists() || success) {
                loadSensorAgents();
                addBehaviour(new SaveToDiskBehaviour(this));
            }
        }
        if (LOGGER_HANDLER) {
            decisionAgentLogger.addHandler(new LoggerAgentLogHandler(this, 60));
        }
        if (DFServices.registerAsService(createServiceDescription(), this)) {
            addBehaviour(new RegisterSensorAgentBehaviour(this));
            addBehaviour(new ReceiveBehaviour(this));
            addBehaviour(new UpdateStatusSensorAgentBehaviour(this, 5000L));
        }
    }

    /**
     * After unregistering the Sensor Agent this method will be executed. Any other cleaning of left overs by the
     * Sensor Agent should be done in here.
     */
    public void unregisterSensorAgentCallback(AID sensoragent) {

    }

    /**
     * The readings will be saved in memory. In the Measurement of the Instruction Set there is a CircularFifoQueue.
     * The amount of readings in memory is changable in the config.properties. storeReading will be executed after
     * the reading is stored in memory. This method is created so the reading could be stored elsewhere as well.
     */
    public void storeReading(double value, InstructionSet is, Sensor sensor, String measurementId) {

    }

    /**
     * After executing the executePlan method, which is set up using the properties in the Instruction Set, this
     * method will be executed.
     */
    public void executePlanCallback(Plan plan) {

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
            DecisionAgent.decisionAgentLogger.log(Logger.INFO, "New Sensor Agent added: " + sensoragent);
        } else {
            this.sensorAgents.put(sensoragent, instructionset);
        }
    }

    public void unregisterSensorAgent(AID sensoragent) {
        this.sensorAgents.remove(sensoragent);
        unregisterSensorAgentCallback(sensoragent);
    }

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
            decide(value, measurement, sensor);
        } catch (NullPointerException npe) {
            decisionAgentLogger.log(Logger.SEVERE, String.format("No measurement found by that ID: %s", measurementId));
        }
        storeReading(value, is, sensor, measurementId);
    }

    private void decide(double reading, Measurement measurement, Sensor sensor) {
        for (Plan plan : measurement.getPlans().getPlans()) {
            if (plan.getCurrentLimit() < plan.getLimit()) {
                if (measurement.getMax() < reading || measurement.getMin() > reading) {
                    executeSensorReadingWarning(sensor, measurement, reading);
                }
                if (plan.getAbove() != 0.0 && (measurement.getMax() * plan.getAbove() < reading) || plan.getBelow() != 0.0 && (reading < measurement.getMax() * plan.getBelow())) {
                    executePlan(sensor, measurement, plan, reading);
                    plan.setCurrentLimit(plan.getCurrentLimit() + 1);
                } else if (!(plan.getCurrentLimit() < 1)){
                    plan.setCurrentLimit(plan.getCurrentLimit() - 1);
                }
            }
        }
    }

    /**
     * Whenever the reading exceeds the minimum value or maximum value specified in the Instruction Set XML, this
     * method will be executed.
     */
    private void executeSensorReadingWarning(Sensor sensor, Measurement measurement, double reading) {
        DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, String.format("Measurement %s from Sensor %s has exceeded the min or max value: %s", measurement.getId(), sensor.getId(), reading));
    }

    private void executePlan(Sensor sensor, Measurement measurement, Plan plan, double reading) {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        String above = "";
        String below = "";
        if (plan.getAbove() != 0.0) {
            above = "Above: " + plan.getAbove() * measurement.getMax();
        }
        if (plan.getBelow() != 0.0) {
            below = "Below: " + plan.getBelow() * measurement.getMax();
        }
        message.setContent(String.format("Sensor %s, Measurement %s has exceeded his described value. %s %s",sensor.getId(), measurement.getId(), below, above));
        message.addReceiver(DFServices.getService(plan.getVia(), this));
        message.addUserDefinedParameter("to", plan.getTo());
        this.send(message);
        executePlanCallback(plan);
    }

    public void executeFallback(Map.Entry<AID, InstructionSet> sensorAgent) {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        InstructionSet is = sensorAgent.getValue();
        Fallback fallback = is.getFallback();
        String fallbackMessage = fallback.getMessage();
        if (fallbackMessage == null) {
            fallbackMessage = "Unregistered sensor agent: " + sensorAgent.getKey();
        }
        message.setContent(fallbackMessage);
        message.addReceiver(DFServices.getService(fallback.getVia(), this));
        message.addUserDefinedParameter("to", fallback.getTo());
        this.send(message);
    }

    /**
     * This method will only be executed when the decisionagent.store_sensor_agents_on_disk=true
     */
    public void storeSensorAgents() {
        if (this.sensorAgents.size() > 0) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"))) {
                decisionAgentLogger.log(Logger.FINE, String.format("Writing %s Sensor Agents w. Instruction Sets to disk.", this.sensorAgents.size()));
                oos.writeObject(this.sensorAgents);
                decisionAgentLogger.log(Logger.FINE, String.format("Written all Sensor Agents w. Instruction Sets to: %s", STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"));
            } catch (IOException e) {
                e.printStackTrace();
                decisionAgentLogger.log(Logger.SEVERE, "Could not write Sensor Agents w. Instruction Sets to disk.");
            }
        }
    }

    private void loadSensorAgents() {
        if(!isUpdatingStatus) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"))) {
                this.sensorAgents = (HashMap) ois.readObject();
                decisionAgentLogger.log(Logger.INFO, String.format("Found %s serialized Sensor Agents w. Instruction Sets.", this.sensorAgents.size()));
            } catch (FileNotFoundException e) {
                decisionAgentLogger.log(Logger.INFO, String.format("Could not find serialized Sensor Agents w. Instruction Sets on disk: %s. Starting fresh.", e.getMessage()));
            } catch (IOException | ClassNotFoundException e) {
                decisionAgentLogger.log(Logger.INFO, String.format("Could not load file, IO Error: %s. Starting fresh.", e.getMessage()));
            }
        }
    }

    private static boolean createDirectoryStructure() {
        return (new File(STORAGE_BASEPATH).mkdirs()); // Return success
    }


    public void setUpdatingStatus(boolean updatingStatus) {
        isUpdatingStatus = updatingStatus;
    }
}
