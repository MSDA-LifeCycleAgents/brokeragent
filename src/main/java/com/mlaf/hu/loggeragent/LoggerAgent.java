package com.mlaf.hu.loggeragent;

import com.mlaf.hu.helpers.DFServices;
import com.mlaf.hu.loggeragent.behaviour.ReceiveBehaviour;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class LoggerAgent extends Agent {
    private static final String SERVICE_NAME = "LOGGER-AGENT";
    private transient java.util.logging.Logger incomingLogger;
    public static final Logger loggerAgentLogger = Logger.getLogger(LoggerAgent.class.getName());

    public LoggerAgent() {
        super();
        DFServices.registerAsService(createServiceDescription(), this);
        incomingLogger = Logger.getLogger("com.mlaf.hu.loggeragent.central");
        addBehaviour(new ReceiveBehaviour(this));
    }

    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName(SERVICE_NAME);
        sd.setType("logger-agent");
        return sd;
    }

    public static CircularFifoQueue<LogRecord> deserializeObject(String s) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (CircularFifoQueue<LogRecord>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            loggerAgentLogger.log(Level.WARNING, "Could not deserialize object", e);
        }
        return null;
    }

    public void logIncommingRecord(LogRecord lr) {
        this.incomingLogger.log(lr);
    }

}
