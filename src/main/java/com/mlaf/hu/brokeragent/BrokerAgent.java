package com.mlaf.hu.brokeragent;

import com.mlaf.hu.brokeragent.behaviour.ReceiveBehaviour;
import com.mlaf.hu.brokeragent.behaviour.SaveBehaviour;
import com.mlaf.hu.brokeragent.behaviour.SendBehaviour;
import com.mlaf.hu.brokeragent.exceptions.InvallidTopicException;
import com.mlaf.hu.brokeragent.exceptions.TopicNotManagedException;
import com.mlaf.hu.helpers.JadeServices;
import jade.core.AID;
import jade.core.Agent;
import jade.core.messaging.TopicManagementHelper;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import javax.xml.bind.JAXB;
import java.io.*;
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
            JadeServices.registerAsService(SERVICE_NAME, "message-broker", "message-broker-ontology", FIPANames.ContentLanguage.FIPA_SL, this);
            topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            if (succes) {
                loadTopics();
                addBehaviour(new SaveBehaviour(this));
            }
            addBehaviour(new SendBehaviour(this, topicHelper));
            addBehaviour(new ReceiveBehaviour(this));
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, "Could not initialize BrokerAgent", e);
            System.exit(1);
        }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ignore) {
        }
    }

    private Topic addNewTopicToBuffer(Topic representationTopic) {
        this.topics.put(representationTopic.getJadeTopic(), representationTopic);
        brokerAgentLogger.log(Logger.FINE, String.format("New topic added: %s, daysToKeepMessages: %s", representationTopic.getTopicName(), representationTopic.getDaysToKeepMessages()));
        return representationTopic;
    }

    private Topic registerTopic(Topic representationTopic, TopicManagementHelper helper) {
        try {
            helper.register(representationTopic.getJadeTopic());
            return this.addNewTopicToBuffer(representationTopic);
        } catch (Exception e) {
            brokerAgentLogger.log(Logger.SEVERE, String.format("Broker %s: ERROR registering to topic %s", this.getLocalName(), representationTopic.getTopicName()), e);
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
        ACLMessage message = new ACLMessage(ACLMessage.CONFIRM);
        message.addReceiver(subscriber);
        if (representationTopic.getTopicName() == null) {
            String content = "The BrokerAgent needs a topic name. Use the following format for subscribing:\n" +
                             "<daysToKeepMessages></daysToKeepMessages>\n<name></name>";
            message.setContent(content);
            message.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            brokerAgentLogger.log(Logger.SEVERE, content);
            return message;
        }
        AID topicAID = this.createTopic(representationTopic.getTopicName(), helper);
        Topic topic = null;
        try {
            topic = this.getTopicByAID(topicAID);
        } catch (InvallidTopicException | TopicNotManagedException e) {
            brokerAgentLogger.log(Logger.INFO, String.format("Topic: %s does not exist yet. Creating topic and registering topic...", representationTopic.getTopicName()));
            representationTopic.setJadeTopic(topicAID);
            topic = this.registerTopic(representationTopic, helper);
        }
        if (topic != null) {
            topic.addToSubscribers(subscriber);
            message.setContent(String.format("Subscribed to the Topic: %s", topic.getTopicName()));
            message.addUserDefinedParameter("name", topic.getTopicName());
        } else {
            message.setContent("Something went wrong while subscribing. See JADE logs.");
            message.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        }
        return message;
    }

    public ACLMessage getMessageFromBuffer(AID subscriber, Topic representationTopic, TopicManagementHelper helper) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
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
                message.setSender(oldestMessage.getPublisher());
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

    public Topic unmarshalTopic(String message) {
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