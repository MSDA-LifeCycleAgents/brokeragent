package com.mlaf.hu.brokeragent.behavior;

import com.mlaf.hu.brokeragent.BrokerAgent;
import com.mlaf.hu.brokeragent.Message;
import com.mlaf.hu.brokeragent.Topic;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.LocalDateTime;
import java.util.Map;

public class ReceiveBehavior extends CyclicBehaviour {
    private final BrokerAgent brokerAgent;

    public ReceiveBehavior(BrokerAgent broke) {
        this.brokerAgent = broke;
    }

    @Override
    public void action() {
        for (Map.Entry<AID, Topic> topicPair : brokerAgent.topics.entrySet()) {
            ACLMessage topicMessage = myAgent.receive(MessageTemplate.MatchTopic(topicPair.getKey()));
            if (topicMessage != null) {
                brokerAgent.storeMessage(new Message(topicMessage.getContent(), topicMessage.getSender(), LocalDateTime.now()), topicPair.getKey());
            } else {
                block();
            }
            Topic topic = topicPair.getValue();
            topic.removeOldMessages();
        }
    }
}
