package com.mlaf.hu.decisionagent.behaviour;

import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.helpers.exceptions.ParseException;
import com.mlaf.hu.models.InstructionSet;
import com.mlaf.hu.models.Topic;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * This behaviour will continuously wait for incoming SUBSCRIBE messages and after having parsed the InstructionSet in XML format
 * add the Sensor Agent to the HashMap<AID, Instructionset>(). If the InstructionSet misses certain XML tags it will respond
 * to the sender with exactly what is missing.
 */

public class RegisterSensorAgentBehaviour extends CyclicBehaviour {
    private final DecisionAgent DA;

    public RegisterSensorAgentBehaviour(DecisionAgent da) {
        this.DA = da;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
        if (message == null) {
            return;
        }
        try {
            InstructionSet is = DA.parseInstructionXml(message.getContent());
            String responseContent = is.checkComposition();
            ACLMessage response = message.createReply();
            response.setContent(responseContent);
            response.setPerformative(ACLMessage.DISCONFIRM);
            if (!is.isMallformed()) {
                response.setPerformative(ACLMessage.CONFIRM);
                response.addReplyTo(getDestination(is));
                DA.registerSensorAgent(message.getSender(), is);
            }
            DA.send(response);
        } catch (ParseException e) {
            DecisionAgent.decisionAgentLogger.log(Logger.SEVERE, e.getMessage());
        }
    }

    private AID getDestination(InstructionSet is) {
        if (is.getMessaging().isDirectToDecisionAgent()) {
            return this.DA.getAID();
        } else {
            try {
                TopicManagementHelper topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
                Topic topic = is.getMessaging().getTopic();
                return topicHelper.createTopic(topic.getTopicName());
            } catch (ServiceException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
