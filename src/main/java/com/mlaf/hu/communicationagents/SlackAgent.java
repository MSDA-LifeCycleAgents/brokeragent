/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlaf.hu.communicationagents;

import com.mlaf.hu.helpers.DFServices;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mlaf.hu.helpers.Configuration;
import jade.lang.acl.MessageTemplate;

/**
 * @author Rogier
 */
public class SlackAgent extends Agent {
    private static final Logger logger = Logger.getLogger(SlackAgent.class.getName());

    @Override
    public void setup() {
        DFServices.registerAsService(createServiceDescription(), this);
        addBehaviour(
                new CyclicBehaviour() {
                    @Override
                    public void action() {
                        ACLMessage aclMessage = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                        if (aclMessage != null) {
                            String message = aclMessage.getContent();
                            String to = aclMessage.getUserDefinedParameter("to");
                            if (message != null && to != null)
                                sendMessage(message, to);
                            else
                                logger.log(Level.WARNING, "Failed to send message: invalid request");
                        }
                    }
                }
        );
    }

    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName("SlackAgent");
        sd.setType("SlackAgent");
        return sd;
    }


    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ignore) {
        }
    }

    private void sendMessage(String message, String channel) {
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
