//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mlaf.hu.brokeragent.helpers;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;

public class TopicPublisher extends Agent {
    protected void setup() {
        try {
            TopicManagementHelper var1 = (TopicManagementHelper)this.getHelper("jade.core.messaging.TopicManagement");
            final AID var2 = var1.createTopic("JADE");
            this.addBehaviour(new TickerBehaviour(this, 10000L) {
                public void onTick() {
                    System.out.println("Agent " + this.myAgent.getLocalName() + ": Sending message about topic " + var2.getLocalName());
                    ACLMessage var1 = new ACLMessage(7);
                    var1.addReceiver(var2);
                    var1.setContent(String.valueOf(this.getTickCount()));
                    this.myAgent.send(var1);
                }
            });
        } catch (Exception var3) {
            System.err.println("Agent " + this.getLocalName() + ": ERROR creating topic \"JADE\"");
            var3.printStackTrace();
        }

    }
}
