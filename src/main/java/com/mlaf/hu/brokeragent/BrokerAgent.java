package com.mlaf.hu.brokeragent;

import com.mlaf.hu.brokeragent.behavior.ReceiveBehavior;
import com.mlaf.hu.brokeragent.behavior.SaveBehavior;
import com.mlaf.hu.brokeragent.behavior.SendBehavior;
import com.mlaf.hu.brokeragent.exceptions.InvallidTopicException;
import com.mlaf.hu.brokeragent.exceptions.TopicNotManagedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.rmi.runtime.Log;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BrokerAgent extends Agent { //TODO berichten en/of topics opslaan op disk via cli of puur config
    private final static String SERVICE_NAME = "BROKER"; //TODO still waiting for configuration singleton
    private final static String STORAGE_BASEPATH = "C:/BrokerAgent/"; //TODO still waiting for configuration singleton
    private final static String STORAGE_FILENAME = "topics"; //TODO still waiting for configuration singleton
    public static final int STORE_INTERVAL_IN_MS = 3000; //TODO still waiting for configuration singleton
    static java.util.logging.Logger brokerAgentLogger = Logger.getLogger("BrokerAgentLogger");
    public HashMap<AID, Topic> topics = new HashMap<>();
    private TopicManagementHelper topicHelper;

    @Override
    protected void setup() {
        try {
            boolean succes = createDirectoryStructure();
            registerAsServiceToDF();
            topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            if (succes) {
                loadTopics();
                addBehaviour(new SaveBehavior(this));
            }
            addBehaviour(new SendBehavior(this, topicHelper));
            addBehaviour(new ReceiveBehavior(this));
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, "Could not initialize BrokerAgent", e);
            System.exit(1);
        }
    }

    private void registerAsServiceToDF() {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(this.getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(SERVICE_NAME);
            sd.setType("message-broker");
            sd.addOntologies("message-broker-ontology");
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            dfd.addServices(sd);
            DFService.register(this, dfd);
            brokerAgentLogger.log(Logger.INFO, ("Registered the BrokerAgent as a service to the DF."));
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private Topic addNewTopicToBuffer(AID topicAID, int daysToKeepMessages) {
        Topic topic = new Topic(topicAID, daysToKeepMessages);
        this.topics.put(topicAID, topic);
        brokerAgentLogger.log(Logger.FINE, String.format("New topic added: %s, daysToKeepMessages: %s", topic.getTopicName(), topic.getDaysToKeepMessages()));
        return topic;
    }

    private Topic registerTopic(AID topic, int daysToKeepMessages, TopicManagementHelper helper) {
        try {
            helper.register(topic);
            return this.addNewTopicToBuffer(topic, daysToKeepMessages);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, String.format("Broker %s: ERROR registering to topic %s", this.getLocalName(), topic), e);
            return null;
        }
    }

    private AID createTopic(String topicName, TopicManagementHelper helper) {
        AID topicAid = null;
        try {
            topicAid = helper.createTopic(topicName);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, String.format("Broker %s: ERROR creating topic %s", this.getLocalName(), topicName), e);
        }
        return topicAid;
    }

    public void storeMessage(Message message, AID topicAID) {
        try {
            Topic topic = this.topics.get(topicAID);
            topic.addToMessages(message);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, "Could not store message", e);
        }
    }

    public ACLMessage addSubscriberToTopic(AID subscriber, Topic representationTopic, TopicManagementHelper helper) {
        ACLMessage message = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
        message.addReceiver(subscriber);
        if (representationTopic.getTopicName() == null) {
            String content = "The BrokerAgent needs a topic name. Use the following format for subscribing:\n" +
                    "<daysToKeepMessages></daysToKeepMessages>\n<name></name>";
            message.setContent(content);
            brokerAgentLogger.log(Logger.SEVERE, content);
            return message;
        }
        AID topicAID = this.createTopic(representationTopic.getTopicName(), helper);
        Topic topic = null;
        try {
            topic = this.getTopicByAID(topicAID);
        } catch (InvallidTopicException | TopicNotManagedException e) {
            brokerAgentLogger.log(Logger.INFO, String.format("Topic: %s does not exist yet. Creating topic and registering topic...", representationTopic.getTopicName()));
            topic = this.registerTopic(topicAID, representationTopic.getDaysToKeepMessages(), helper);
        }
        if (topic != null) {
            topic.addToSubscribers(subscriber);
            message.setContent(String.format("Subscribed to the Topic %s", topic.getJadeTopic()));
        } else {
            message.setContent("Something went wrong while subscribing. See JADE logs.");
        }
        message.setPerformative(ACLMessage.CONFIRM);
        return message;
    }

    public ACLMessage getMessageFromBuffer(AID subscriber, Topic representationTopic, TopicManagementHelper helper) {
        ACLMessage message = new ACLMessage(ACLMessage.CONFIRM);
        if (representationTopic.getTopicName() == null) {
            String content = "The BrokerAgent needs a topic name. Use the following format for requesting:\n" +
                    "<name></name>";
            message.setContent(content);
            brokerAgentLogger.log(Logger.SEVERE, content);
            return message;
        }
        AID topicAID = createTopic(representationTopic.getTopicName(), helper);
        message.addReceiver(subscriber);
        try {
            Topic topic = this.getTopicByAID(topicAID);
            if (topic.getSubscriber(subscriber) != null) {
                Message oldestMessage = topic.getOldestMessage();
                message.setContent(oldestMessage.getContent());
            } else {
                message.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                String content = String.format("Subscriber %s not subscribed for this Topic: %s. " +
                        "Set performative to subscribe and include the following content:" +
                        "\n <daysToKeepMessages></daysToKeepMessages>\n<name></name>", subscriber, representationTopic.getTopicName());
                message.setContent(content);
                brokerAgentLogger.log(Logger.SEVERE, content);
            }
        } catch (InvallidTopicException | TopicNotManagedException e) {
            message.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            message.setContent(e.getMessage());
        }
        return message;
    }

    public Topic parseMessage(String message) {
        Topic topic = new Topic();
        try {
            topic = JAXB.unmarshal(new StringReader(message), Topic.class);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, String.format("XML Request is not well formed. File: %s", message));
        }
        return topic;
    }

    private Topic getTopicByAID(AID aid) throws InvallidTopicException, TopicNotManagedException {
        //TODO Let all methods use this to get topic
        if (!topicHelper.isTopic(aid)) {
            throw new InvallidTopicException(aid.getName() + " is not a valid topic AID");
        }
        if (!this.topics.containsKey(aid)) {
            throw new TopicNotManagedException(aid.getName() + " is not managed");
        }
        return this.topics.get(aid);
    }

    public void storeTopics() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"))) {
            oos.writeObject(this.topics);
            brokerAgentLogger.log(Logger.FINE, String.format("Written all topics to: %s", STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"));
        } catch (IOException e) {
            brokerAgentLogger.log(Logger.SEVERE, String.format("Could not write topics to disk %nError %s", e.getMessage()));
        }
    }

    private void loadTopics() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_BASEPATH + STORAGE_FILENAME + ".ser"))) {
            this.topics = (HashMap) ois.readObject();
            brokerAgentLogger.log(Logger.INFO, String.format("Found serialized topics: \n%s.", HashMapToString()));
        } catch (FileNotFoundException e) {
            brokerAgentLogger.log(Logger.INFO, String.format("Could not find serialized topics on disk: %s. Starting fresh.", e.getMessage()));
        } catch (IOException | ClassNotFoundException e) {
            brokerAgentLogger.log(Logger.INFO, String.format("Could not load file, IO Error: %s. Starting fresh.", e.getMessage()));
        }
    }

    private String HashMapToString() {
        StringBuilder toString = new StringBuilder();
        for (Map.Entry<AID, Topic> entry : this.topics.entrySet()) {
            toString.append(entry.getKey()).append(" : ").append(entry.getValue().toString()).append(" \n");
        }
        return toString.toString();
    }

    private static boolean createDirectoryStructure() {
        return (new File(STORAGE_BASEPATH).mkdirs()); // Return success
    }

}