package com.mlaf.hu.decisionagent.behavior;

import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.models.InstructionSet;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RegisterBehavior extends CyclicBehaviour{
    private final DecisionAgent DA;

    public RegisterBehavior(DecisionAgent da) {
        this.DA = da;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
        InstructionSet is  = DA.parseInstructionXml(message.getContent());
        DA.registerSensorAgent(message.getSender(), is);
        String responseContent = is.testIntegrity();
        int performative = is.isNotInteger() ? ACLMessage.DISCONFIRM : ACLMessage.CONFIRM;
        ACLMessage response = new ACLMessage(performative);
        response.setContent(responseContent);
        DA.send(response);
    }


}
