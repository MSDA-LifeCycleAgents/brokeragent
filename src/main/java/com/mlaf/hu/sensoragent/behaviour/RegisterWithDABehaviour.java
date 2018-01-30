package com.mlaf.hu.sensoragent.behaviour;

import com.mlaf.hu.sensoragent.SensorAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import jade.util.leap.Iterator;

import java.time.LocalDateTime;

public class RegisterWithDABehaviour extends CyclicBehaviour {
    private final SensorAgent sa;
    private LocalDateTime continueAfter = LocalDateTime.now();

    public RegisterWithDABehaviour(SensorAgent a) {
        super(a);
        this.sa = a;
    }

    @Override
    public void action() {
        ACLMessage refuseSubscription = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM));
        if (refuseSubscription != null) {
            SensorAgent.sensorAgentLogger.log(Logger.WARNING, "Reason for refusing: \n" + refuseSubscription.getContent());
            this.sa.onReceivingRefuseRegistration();
        }
        onSubscribe();
        if (!this.sa.isRegistered() && LocalDateTime.now().isAfter(continueAfter)) {
            handleRegistration();

        } else if (this.sa.isRegistered()) {
            checkIfUnregistered();
        }
    }

    private void checkIfUnregistered() {
        MessageTemplate performative = MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM);
        MessageTemplate ontology = MessageTemplate.MatchOntology("sensor-agent-register");
        ACLMessage unsubscribed = myAgent.receive(MessageTemplate.and(performative, ontology));
        if (unsubscribed != null) {
            this.sa.setRegistered(false);
        }
    }

    private void handleRegistration() {
        this.sa.registerWithDA();
        this.continueAfter = LocalDateTime.now().plusSeconds(20);
    }

    private void onSubscribe() {
        ACLMessage subscribed = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        if (subscribed == null) {
            return;
        }
        Iterator it = subscribed.getAllReplyTo();
        while(it.hasNext()) {
            AID newDest = (AID) it.next();
            this.sa.setDestination(newDest);
        }
        this.sa.setRegistered(true);
        SensorAgent.sensorAgentLogger.log(Logger.INFO, "Registered with the Decision Agent.\nStarting to send data from buffer.");
        SendBufferBehaviour sendBehaviour = new SendBufferBehaviour(this.sa);
        this.sa.addBehaviour(sendBehaviour);
        sendBehaviour.action();
    }
}
