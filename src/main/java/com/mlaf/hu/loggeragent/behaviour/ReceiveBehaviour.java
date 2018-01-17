package com.mlaf.hu.loggeragent.behaviour;

import com.mlaf.hu.loggeragent.LoggerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ReceiveBehaviour extends CyclicBehaviour {
    private LoggerAgent agent;

    public ReceiveBehaviour(LoggerAgent agent){
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage receivedMessage = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        //TODO Check more specific than performative
        if (receivedMessage == null) {
            block();
        } else {
            handleSerializedMessage(receivedMessage);
        }

    }

    private void handleSerializedMessage(ACLMessage message) {
        CircularFifoQueue<LogRecord> receivedLogs = LoggerAgent.deserializeObject(message.getContent());
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
