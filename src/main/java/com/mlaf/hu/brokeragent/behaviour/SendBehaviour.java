package com.mlaf.hu.brokeragent.behaviour;

import com.mlaf.hu.brokeragent.BrokerAgent;
import com.mlaf.hu.brokeragent.Topic;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SendBehaviour extends CyclicBehaviour {
    private final BrokerAgent brokerAgent;
    private final TopicManagementHelper topicHelper;

    public SendBehaviour(BrokerAgent broker, TopicManagementHelper topicHelper) {
        this.brokerAgent = broker;
        this.topicHelper = topicHelper;
    }

    @Override
    public void action() {
        ACLMessage subscriberMessage = brokerAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
        ACLMessage getContentMessage = brokerAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        ACLMessage responseSubscription = null;
        ACLMessage responseGetContent = null;
        Topic representationTopic = brokerAgent.unmarshalTopic(subscriberMessage.getContent());
        if (subscriberMessage != null) {
            responseSubscription = brokerAgent.addSubscriberToTopic(subscriberMessage.getSender(), representationTopic, topicHelper);
        }
        if (getContentMessage != null) {
            responseGetContent = brokerAgent.getMessageFromBuffer(getContentMessage.getSender(), representationTopic, topicHelper);
        }
        if (responseSubscription != null) {
            brokerAgent.send(responseSubscription);
        }
        if (responseGetContent != null) {
            brokerAgent.send(responseGetContent);
        }
    }
}
