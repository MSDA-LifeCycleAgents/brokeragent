package com.mlaf.hu.loggeragent;

import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerAgentLogHandler extends Handler {
    private Agent agent;
    private CircularFifoQueue<LogRecord> queue;
    private ServiceDiscovery sdLoggerAgent;
    private Logger logger = Logger.getLogger(LoggerAgentLogHandler.class.getName());
    private LocalDateTime lastUpdateSent = LocalDateTime.now();
    private int maxSendDelay;


    public LoggerAgentLogHandler(Agent agent, int maxSendDelaySeconds) {
        this.agent = agent;
        this.queue = new CircularFifoQueue<>();
        this.sdLoggerAgent = new ServiceDiscovery(agent, ServiceDiscovery.SD_LOGGER_AGENT());
        this.maxSendDelay = maxSendDelaySeconds;
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
            message.addReceiver(sdLoggerAgent.ensureAID(60));
            String serialized = serializeObject(this.queue);
            if (serialized == null) {
                // Did not receive log list, stop sending of buffer
                return;
            }
            message.setContent(serialized);
            this.queue.clear();
            agent.send(message);
            lastUpdateSent = LocalDateTime.now();
        } catch (ServiceDiscoveryNotFoundException e) {
            logger.log(Level.INFO, "Could not find LoggerAgent.");
        }
    }

    private boolean shouldSendBuffer() {
        return (this.lastUpdateSent.plusSeconds(maxSendDelay).isAfter(LocalDateTime.now()));
    }

    private String serializeObject(CircularFifoQueue<LogRecord> o) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(o);
            out.flush();
            return out.toString();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not serialize object", e);
        }
        return null;
    }

    @Override
    public void flush() {
        sendBuffer();
    }

    @Override
    public void close()  {
        if (!queue.isEmpty()) {
            flush();
        }
    }

}
