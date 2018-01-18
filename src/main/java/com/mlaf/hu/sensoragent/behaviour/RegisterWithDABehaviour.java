package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RegisterWithDABehaviour extends TickerBehaviour{
    private final SensorAgent sa;


    public RegisterWithDABehaviour(SensorAgent a, long period) {
        super(a, period);
        this.sa = a;
    }

    @Override
    protected void onTick() {
        if (!this.sa.isRegistered()) {
            this.sa.registerWithDA();
            ACLMessage subscribed = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
            if (subscribed == null) {
                block();
            }
            this.sa.setRegistered(true);
            this.sa.addBehaviour(new SendBufferBehaviour(this.sa));
        }
    }
}
