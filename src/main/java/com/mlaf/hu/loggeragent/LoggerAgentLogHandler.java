package com.mlaf.hu.loggeragent;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

public class LoggerAgentLogHandler extends Handler {
    private Agent agent;
    private XMLFormatter xmlFormatter;
    public LoggerAgentLogHandler(Agent agent) {
        this.agent = agent;
        xmlFormatter = new XMLFormatter();
    }


    @Override
    public void publish(LogRecord record) {
        String formatted = xmlFormatter.format(record);
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("henk", true));
        message.setContent(formatted);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

}
