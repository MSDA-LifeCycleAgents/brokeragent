package com.mlaf.hu.loggeragent.behavior;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveBehavior extends CyclicBehaviour {
    private Agent agent;

    public ReceiveBehavior(Agent agent){
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage receivedMessage = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        //TODO Check more specific than performative
        if (receivedMessage == null) {
            block();
        } else {
            handleSerializedMessage(receivedMessage);
        }

    }

    private void handleSerializedMessage(ACLMessage message) {

    }
}
