package com.mlaf.hu.loggeragent;

import com.mlaf.hu.helpers.DFServices;
import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.loggeragent.behaviour.ReceiveBehaviour;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class LoggerAgent extends Agent {
    private static final String SERVICE_NAME = "LOGGER-AGENT";
    private transient java.util.logging.Logger incomingLogger;
    public static final Logger loggerAgentLogger = Logger.getLogger(LoggerAgent.class.getName());

    public LoggerAgent() {
        super();
        incomingLogger = Logger.getLogger("com.mlaf.hu.loggeragent.central");
        ConsoleHandler handler = new ConsoleHandler();
        incomingLogger.addHandler(handler);
    }

    @Override
    protected void setup(){
        DFServices.registerAsService(this.createServiceDescription(), this);
        addBehaviour(new ReceiveBehaviour(this));
    }

    public ServiceDescription createServiceDescription() {
        return ServiceDiscovery.SD_LOGGER_AGENT();
    }

    public static CircularFifoQueue<LogRecord> deserializeObjectB64(String s) {
        try {
            byte[] decoded = Base64.getDecoder().decode(s);
            ByteArrayInputStream bis = new ByteArrayInputStream(decoded);
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
