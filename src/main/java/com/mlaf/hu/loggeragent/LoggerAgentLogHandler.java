package com.mlaf.hu.loggeragent;

import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

public class LoggerAgentLogHandler extends Handler {
    private Agent agent;
    private CircularFifoQueue<LogRecord> queue;
    private ServiceDiscovery sd_logger_agent;


    public LoggerAgentLogHandler(Agent agent) {
        this.agent = agent;
        this.queue = new CircularFifoQueue<>();
        this.sd_logger_agent = new ServiceDiscovery(agent, ServiceDiscovery.SD_LOGGER_AGENT());
    }


    @Override
    public void publish(LogRecord record) {
        this.queue.add(record);
        if (shouldSendBuffer()) {
            sendBuffer();
        }
    }

    private void sendBuffer() {
        try {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(sd_logger_agent.ensureAID(60));
            String serialized = serializeObject(this.queue);
            if (serialized == null) {
                //TODO Logging (Logception?)
                return;
            }
            message.setContent(serialized);
            this.queue.clear();
            agent.send(message);
        } catch (ServiceDiscoveryNotFoundException e) {
            //TODO Logging (Logception?)
        }
    }

    private boolean shouldSendBuffer() {
        return true;
    }

    private String serializeObject(Object o) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(o);
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void flush() {
        sendBuffer();
    }

    @Override
    public void close() throws SecurityException {

    }

}
