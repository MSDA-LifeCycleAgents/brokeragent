package com.mlaf.hu.brokeragent.helpers;

import com.mlaf.hu.helpers.DFServices;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class TopicSubscriber extends Agent {
    private boolean subscribed = false;

    protected void setup() {
        try {
            AID brokerService = DFServices.getService("message-broker", this);
            this.addBehaviour(new TickerBehaviour(this, 10000L) {
                @Override
                protected void onTick() {
                    if (!subscribed) {
                        System.out.println("Subscribing to the JADE topic.");
                        subscribeToTopic(brokerService);
                    } else {
                        System.out.println("Requesting message from the JADE topic.");
                        requestFromTopic(brokerService);
                    }
                }
            });
            this.addBehaviour(new CyclicBehaviour(this) {
                public void action() {
                    ACLMessage var1 = this.myAgent.receive();
                    if (var1 != null) {
                        if (!subscribed && var1.getPerformative() == ACLMessage.CONFIRM) {
                            subscribed = true;
                        } else if (subscribed && var1.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                            subscribed = false;
                        }
                        System.out.println("Agent " + this.myAgent.getLocalName() + ": Message received. Content is " + var1.getContent() + " performative is: " + var1.getPerformative());
                    } else {
                        this.block();
                    }

                }
            });
        } catch (Exception var3) {
            System.err.println("Agent " + this.getLocalName() + ": ERROR registering to topic \"JADE\"");
            var3.printStackTrace();
        }
    }

    private void subscribeToTopic(AID service) {
        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.addReceiver(service);
        msg.setLanguage("English");
        msg.setContent("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<Topic>" +
                        "<daysToKeepMessages>2</daysToKeepMessages>" +
                        "<name>JADE</name>" +
                        "</Topic>");
        send(msg);
    }

    private void requestFromTopic(AID service) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(service);
        msg.setLanguage("English");
        msg.setContent("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<Topic>" +
                        "<name>JADE</name>" +
                        "</Topic>");
        send(msg);
    }

}
