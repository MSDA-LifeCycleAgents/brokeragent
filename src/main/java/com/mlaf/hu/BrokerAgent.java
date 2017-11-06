package com.mlaf.hu;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;

public class BrokerAgent extends Agent {

	protected void setup() {
		try {
			// Periodically send messages about topic "JADE"
			TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			final AID topic = topicHelper.createTopic("JADE");
			addBehaviour(new TickerBehaviour(this, 10000) {
				public void onTick() {
					System.out.println("Agent "+myAgent.getLocalName()+": Sending message about topic "+topic.getLocalName());
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.addReceiver(topic);
					msg.setContent(String.valueOf(getTickCount()));
					myAgent.send(msg);
				}
			} );
		}
		catch (Exception e) {
			System.err.println("Agent "+getLocalName()+": ERROR creating topic \"JADE\"");
			e.printStackTrace();
		}
	}
}