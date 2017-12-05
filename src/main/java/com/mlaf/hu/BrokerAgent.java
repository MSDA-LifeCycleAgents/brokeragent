package com.mlaf.hu;

import com.mlaf.hu.behavior.ReceiveBehavior;
import com.mlaf.hu.behavior.SendBehavior;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.HashMap;

public class BrokerAgent extends Agent { //TODO berichten en/of topics opslaan op disk via cli
    public HashMap<AID, Topic> topics = new HashMap<>();
    private static final int DAYS_TO_KEEP_MESSAGES = 1; //TODO cli
    public static java.util.logging.Logger brokerAgentLogger = Logger.getLogger("BrokerAgentLogger");
    private final static String SERVICE_NAME = "BROKER";

    @Override
    protected void setup() {
        try {
            registerToDF();
            final TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            addBehaviour(new SendBehavior(this, topicHelper));
            addBehaviour(new ReceiveBehavior(this));
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, "Could not initialize BrokerAgent", e);
            System.exit(1);
        }
    }

    private void registerToDF() {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(this.getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(SERVICE_NAME);
            sd.setType("message-broker");
            // Agents that want to use this service need to "know" the weather-forecast-ontology
            sd.addOntologies("message-broker-ontology");
            // Agents that want to use this service need to "speak" the FIPA-SL language
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            dfd.addServices(sd);
            DFService.register(this, dfd);
            brokerAgentLogger.log(Logger.INFO, ("Registered the BrokerAgent as a service to the DF."));
        } catch (FIPAException e) {
            e.printStackTrace();
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

    public void storeMessage(Message message, AID topicAID) {
        try {
            Topic topic = this.topics.get(topicAID);
            topic.addToMessages(message);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, "Could not store message", e);
        }
    }

    public void giveMessageToSubscriber(AID subscriber, Message message, int performative) {
        ACLMessage msg = new ACLMessage(performative);
        msg.addReceiver(subscriber);
        msg.setLanguage("English");
        msg.setContent(message.getContent());
        send(msg);
    }

    public Message getMessageFromBuffer(AID subscriber, String topicName, TopicManagementHelper helper) throws ServiceException {
        AID topicAID = createTopic(topicName, helper);
        if (!this.topics.containsKey(topicAID)) {
            brokerAgentLogger.log(Logger.INFO, () -> String.format("Topic: %s does not exist yet. Creating topic and registering topic...", topicName));
            this.registerTopic(topicAID, helper);
            return null;
        }

        Topic topic = this.topics.get(topicAID);
        if (topic.getSubscriber(subscriber) == null) {
            brokerAgentLogger.log(Logger.INFO, () -> String.format("Subscriber: %s did not register to the topic yet. Adding subscriber to the topic...", subscriber.getName()));
            topic.addToSubscribers(subscriber);
            return null;
        }

        return topic.getOldestMessage();
    }

    public String normalizeMessage(String message) {
        //TODO make this useful
        return message;
    }
}