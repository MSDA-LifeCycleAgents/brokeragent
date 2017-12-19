package com.mlaf.hu.decisionagent.representationmodels;

import com.mlaf.hu.brokeragent.Topic;

public class Messaging {
    private boolean directToDecisionAgent;
    private Topic topic;

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
}
