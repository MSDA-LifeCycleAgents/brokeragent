package com.mlaf.hu.loggeragent.behaviour;

import com.mlaf.hu.loggeragent.LoggerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ReceiveBehaviour extends CyclicBehaviour {
    /**
     * This behaviour will receive the messages sent by LoggerAgentLogHandlers, deserialize them and add them to the
     * LoggerAgent.incomingLogger.
     */

    private LoggerAgent agent;

    public ReceiveBehaviour(LoggerAgent agent){
        this.agent = agent;
    }


    /**
     * Receives messages matching Performative:INFORM AND Ontology:logger-agent-logrecords.
     * Received messages will be sent to handleSerializedMessage().
     * Blocks if no message is received.
     */
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchOntology("logger-agent-logrecords")
        );
        ACLMessage receivedMessage = myAgent.receive(mt);
        if (receivedMessage == null) {
            block();
        } else {
            handleSerializedMessage(receivedMessage);
        }

    }

    /**
     * Handles the received serialized message.
     * Deserializes the message and submits all the messages to loggerAgent.incomingLogger
     * @param message ACL Message to be deserialized and processed.
     */
    private void handleSerializedMessage(ACLMessage message) {
        CircularFifoQueue<LogRecord> receivedLogs = LoggerAgent.deserializeObjectB64(message.getContent());
        if (receivedLogs == null) {
            LoggerAgent.loggerAgentLogger.log(Level.WARNING, "Could not deserialize payload in message received from " + message.getSender());
            return;
        }
        while (!receivedLogs.isEmpty()) {
            LogRecord lr = receivedLogs.poll();
            agent.logIncommingRecord(lr);
        }


    }
}
