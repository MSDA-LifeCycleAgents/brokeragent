package com.mlaf.hu.behavior;

import com.mlaf.hu.BrokerAgent;
import com.mlaf.hu.Message;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class SendBehavior extends CyclicBehaviour {
    private final BrokerAgent brokerAgent;
    private final TopicManagementHelper topicHelper;

    public SendBehavior(BrokerAgent broke, TopicManagementHelper topicHelper) {
        this.brokerAgent = broke;
        this.topicHelper = topicHelper;
    }

    @Override
    public void action() {
        ACLMessage subscriberMessage = brokerAgent.receive();
        if (subscriberMessage == null) {
            block();
            return;
        }
        AID subscriber = subscriberMessage.getSender();
        String topicName = brokerAgent.normalizeMessage(subscriberMessage.getContent());
        try {
            Message bufferedMessage = brokerAgent.getMessageFromBuffer(subscriber, topicName, topicHelper);
            int performative = ACLMessage.CONFIRM;
            if (bufferedMessage == null) {
                bufferedMessage = new Message("No more messages in this queue.");
                performative = ACLMessage.DISCONFIRM;
            }
            brokerAgent.giveMessageToSubscriber(subscriber, bufferedMessage, performative);
        } catch (ServiceException e) {
            BrokerAgent.brokerAgentLogger.log(Logger.SEVERE, "Could not get messages from buffer", e);
        }
    }
}
