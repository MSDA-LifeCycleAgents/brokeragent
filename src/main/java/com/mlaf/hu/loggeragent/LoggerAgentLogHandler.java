package com.mlaf.hu.loggeragent;

import com.mlaf.hu.helpers.ServiceDiscovery;
import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * LogHandler which can be added to Java loggers. Messages published to this handler will be sent to the LoggerAgent.
 */
public class LoggerAgentLogHandler extends Handler {
    private Agent agent;
    private CircularFifoQueue<LogRecord> queue;
    private ServiceDiscovery sdLoggerAgent;
    private Logger logger = Logger.getLogger(LoggerAgentLogHandler.class.getName());
    private LocalDateTime lastUpdateSent = LocalDateTime.now();
    private int maxSendDelay;
    private TimerTask sendTask;
    private Timer timer;


    /**
     * LogHandler which can be added to Java loggers. Messages published to this handler will be sent to the LoggerAgent.
     * @param agent The agent to which the log handler is connected.
     * @param maxSendDelaySeconds Max interval between sending the log message buffer to the logger agent.
     */
    public LoggerAgentLogHandler(Agent agent, int maxSendDelaySeconds) {
        this.agent = agent;
        this.queue = new CircularFifoQueue<>();
        this.sdLoggerAgent = new ServiceDiscovery(agent, ServiceDiscovery.SD_LOGGER_AGENT());
        this.maxSendDelay = maxSendDelaySeconds;
        timer = new Timer();

        this.sendTask = new TimerTask() {
            boolean lock = false;
            @Override
            public void run() {
                if (lock) { return; }
                if (shouldSendBuffer()) {
                    lock = true;
                    try {
                        sendBuffer();
                        // Clean up unnecessarily scheduled tasks;
                        timer.purge();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Could not send buffer, releasing lock", e);
                    } finally {
                        lock = false;
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(sendTask, 0, 1000);
    }


    @Override
    public void publish(LogRecord record) {
        this.queue.add(record);
    }

    /**
     * Send the current buffer to the LoggerAgent.
     */
    private void sendBuffer() {
        try {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            AID loggerAgent = sdLoggerAgent.ensureAID(20);
            message.addReceiver(loggerAgent);
            String serialized = serializeObjectB64(this.queue);
            if (serialized == null) {
                // Did not receive log list, stop sending of buffer
                return;
            }
            message.setContent(serialized);
            message.setOntology("logger-agent-logrecords");
            this.queue.clear();
            agent.send(message);
            lastUpdateSent = LocalDateTime.now();
        } catch (ServiceDiscoveryNotFoundException e) {
            logger.log(Level.INFO, "Could not find LoggerAgent.");
        }
    }


    private boolean shouldSendBuffer() {
        return (LocalDateTime.now().isAfter(this.lastUpdateSent.plusSeconds(maxSendDelay)));
    }

    /**
     * Serialize and Base64 encode a CircularFifoQueue of LogRecords
     * @param o Queue to be encoded.
     * @return Base64 encoded string containing the Queue
     */
    private String serializeObjectB64(CircularFifoQueue<LogRecord> o) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(o);
            out.flush();
            byte[] base64Enc = Base64.getEncoder().encode(outputStream.toByteArray());
            return new String(base64Enc);
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
        sendTask.cancel();
        if (!queue.isEmpty()) {
            flush();
        }
    }

}
