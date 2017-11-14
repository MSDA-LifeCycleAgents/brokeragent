package com.mlaf.hu.helpers;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class TopicSubscriber extends Agent {
    protected void setup() {
        try {
            AID brokerService = getService();
            this.addBehaviour(new TickerBehaviour(this, 10000L) {
                @Override
                protected void onTick() {
                    System.out.println("Subscribing to the JADE topic.");
                    requestFromTopic(brokerService);
                }
            });
            this.addBehaviour(new CyclicBehaviour(this) {
                public void action() {
                    ACLMessage var1 = this.myAgent.receive();
                    if (var1 != null) {
                        System.out.println("Agent " + this.myAgent.getLocalName() + ": Message received. Content is " + var1.getContent());
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

    private AID getService() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("message-broker");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            if (result.length > 0)
                System.out.println(result[0].getName());
                return result[0].getName();
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return null;
    }

    private void requestFromTopic(AID service) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(service);
        msg.setLanguage("English");
        msg.setContent("JADE");
        send(msg);
    }
}
