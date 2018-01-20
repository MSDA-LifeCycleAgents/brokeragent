package com.mlaf.hu.communication;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mlaf.hu.helpers.Configuration;

/**
 * @author Rogier
 */
public class SlackAgent extends CommunicationAgent {
    private static final Logger logger = Logger.getLogger(SlackAgent.class.getName());

    @Override
    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName("SlackAgent");
        sd.setType("SlackAgent");
        return sd;
    }
    
    @Override
    protected void send(String message, String channel) {
        Configuration config = Configuration.getInstance();
        String authToken = config.getProperty("slack.auth_token");

        if (channel == null)
            channel = config.getProperty("slack.default_channel");

        try {
            SlackSession session = SlackSessionFactory.createWebSocketSlackSession(authToken);
            session.connect();
            SlackChannel chan = session.findChannelByName(channel);
            session.sendMessage(chan, message);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to send message: {0}", e.toString());
        }
    }
}
