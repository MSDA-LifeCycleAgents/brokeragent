package com.mlaf.hu;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class BrokerAgent extends Agent {
    private HashMap<AID, Topic> topics = new HashMap<>();
    private static final int DAYS_TO_KEEP_MESSAGES = 1;
    private static java.util.logging.Logger brokerAgentLogger = Logger.getLogger("BrokerAgentLogger");

    @Override
    protected void setup() {
        try {
            final TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            this.addBehaviour(new CyclicBehaviour(this) {
                @Override
                public void action() {
                    ACLMessage subscriberMessage = receive();
                    if (subscriberMessage == null) {
                        block();
                        return;
                    }
                    AID subscriber = subscriberMessage.getSender();
                    String topicName = normalizeMessage(subscriberMessage.getContent());
                    try {
                        Message bufferedMessage = getMessageFromBuffer(subscriber, topicName, topicHelper);
                        if (bufferedMessage == null) {
                            bufferedMessage = new Message("No more messages in this queue.");
                        }
                        giveMessageToSubscriber(subscriber, bufferedMessage);
                    } catch (ServiceException e) {
                        brokerAgentLogger.log(Logger.SEVERE, "Could not get messages from buffer", e);
                    }
                }
            });
            this.addBehaviour(new CyclicBehaviour() {
                @Override
                public void action() {
                    for (Map.Entry<AID, Topic> topicPair : topics.entrySet()) {
                        ACLMessage topicMessage = myAgent.receive(MessageTemplate.MatchTopic(topicPair.getKey()));
                        if (topicMessage != null) {
                            storeMessage(new Message(topicMessage.getContent(), topicMessage.getSender(), LocalDateTime.now()), topicPair.getKey());
                        } else {
                            block();
                        }
                        Topic topic = topicPair.getValue();
                        topic.removeOldMessages();
                    }
                }
            });
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, "Could not initialize BrokerAgent", e);
            System.exit(1);
        }
    }

    private void addNewTopicToBuffer(AID topicAID) {
        Topic topic = new Topic(topicAID, DAYS_TO_KEEP_MESSAGES);
        this.topics.put(topicAID, topic);
    }

    private void registerTopic(AID topic, TopicManagementHelper helper) throws ServiceException {
        try {
            helper.register(topic);
            this.addNewTopicToBuffer(topic);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, String.format("Broker %s: ERROR registering to topic %s", this.getLocalName(), topic), e);

        }
    }

    private AID createTopic(String topicName, TopicManagementHelper helper) {
        AID topic = null;
        try {
            topic = helper.createTopic(topicName);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, String.format("Broker %s: ERROR creating topic %s", this.getLocalName(), topicName), e);
        }
        return topic;
    }

    private void storeMessage(Message message, AID topicAID) {
        try {
            Topic topic = this.topics.get(topicAID);
            topic.addToMessages(message);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, "Could not store message", e);
        }
    }

    private void giveMessageToSubscriber(AID subscriber, Message message) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(subscriber);
        msg.setLanguage("English");
        msg.setContent(message.getContent());
        send(msg);
    }

    private Message getMessageFromBuffer(AID subscriber, String topicName, TopicManagementHelper helper) throws ServiceException {
        AID topicAID = createTopic(topicName, helper);
        if (!this.topics.containsKey(topicAID)) {
            brokerAgentLogger.log(Logger.INFO, () -> String.format("Topic: %s does not exist yet. Creating topic and registering topic...", topicName));
            this.registerTopic(topicAID, helper);
            return null;
        }

        Topic topic = this.topics.get(topicAID);
        if (topic.getSubscriber(subscriber) == null) {
            brokerAgentLogger.log(Logger.INFO, () -> String.format("Subscriber: %s did not register to the topic yet. Adding subscriber to the topic...", subscriber));
            topic.addToSubscribers(subscriber);
            return null;
        }

        return topic.getLastMessage();
    }

    private String normalizeMessage(String message) {
        //TODO make this useful
        return message;
    }
}