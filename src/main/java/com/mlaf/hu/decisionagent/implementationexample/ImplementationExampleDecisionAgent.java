package com.mlaf.hu.decisionagent.implementationexample;

import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.helpers.JadeServices;
import com.mlaf.hu.models.Plan;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ImplementationExampleDecisionAgent extends DecisionAgent {

    @Override
    public void unregisterSensorAgent(AID sensoragent) {

    }

    @Override
    public void storeReading(double value) {

    }

    @Override
    public void executePlan(Plan plan) {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent(plan.getMessage());
        message.addReceiver(JadeServices.getService(plan.getVia(), this));
        message.addUserDefinedParameter("to", plan.getTo());
        this.send(message);
    }

}
