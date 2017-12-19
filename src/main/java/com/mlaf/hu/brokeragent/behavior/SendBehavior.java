package com.mlaf.hu.brokeragent.behavior;

import com.mlaf.hu.brokeragent.BrokerAgent;
import com.mlaf.hu.brokeragent.Message;
import com.mlaf.hu.brokeragent.Topic;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class SendBehavior extends CyclicBehaviour {
    private final BrokerAgent brokerAgent;
    private final TopicManagementHelper topicHelper;

    public SendBehavior(BrokerAgent broker, TopicManagementHelper topicHelper) {
        this.brokerAgent = broker;
        this.topicHelper = topicHelper;
    }

    @Override
    public void action() {
        ACLMessage subscriberMessage = brokerAgent.receive(); //FIXME use messagetemplates for better code quality
        if (subscriberMessage == null) {
            block();
            return;
        }
        AID subscriber = subscriberMessage.getSender();
        int incomingPerformative = subscriberMessage.getPerformative();
        ACLMessage message = null;
        Topic representationTopic = brokerAgent.parseMessage(subscriberMessage.getContent());
        if (incomingPerformative == ACLMessage.SUBSCRIBE) {
            message = brokerAgent.addSubscriberToTopic(subscriber, representationTopic, topicHelper);
        } else if (incomingPerformative == ACLMessage.REQUEST) {
            message = brokerAgent.getMessageFromBuffer(subscriber, representationTopic, topicHelper);
        } else {
            block();
            return;
        }
        if (message != null) {
            brokerAgent.send(message);
        }
    }
}
