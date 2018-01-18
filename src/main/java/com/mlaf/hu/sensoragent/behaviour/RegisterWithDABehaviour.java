package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.brokeragent.behaviour.SendBehaviour;
import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

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
            ACLMessage reasonNotSubscribed = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM));
            if (reasonNotSubscribed != null) {
                SensorAgent.sensorAgentLogger.log(Logger.WARNING, "Reason for refusing: \n" + reasonNotSubscribed.getContent());
            }
            if (subscribed == null) {
                block();
                return;
            }
            this.sa.setRegistered(true);
            SensorAgent.sensorAgentLogger.log(Logger.INFO, "Registered with the Decision Agent. Starting to send data from buffer.");
            SendBufferBehaviour sendBehaviour = new SendBufferBehaviour(this.sa);
            this.sa.addBehaviour(sendBehaviour);
            sendBehaviour.action();
            this.stop();
        }
    }
}
