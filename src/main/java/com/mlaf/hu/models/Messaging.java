package com.mlaf.hu.models;

import com.mlaf.hu.brokeragent.Topic;

public class Messaging {
    private boolean directToDecisionAgent;
    private Topic topic;
    private boolean registeredToTopic = false;

    public boolean isDirectToDecisionAgent() {
        return directToDecisionAgent;
    }

    public void setDirectToDecisionAgent(boolean directToDecisionAgent) {
        this.directToDecisionAgent = directToDecisionAgent;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public boolean isRegisteredToTopic() {
        return registeredToTopic;
    }

    public void setRegisteredToTopic(boolean registeredToTopic) {
        this.registeredToTopic = registeredToTopic;
    }
}
