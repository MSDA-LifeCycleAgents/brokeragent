package com.mlaf.hu.helpers;

import com.mlaf.hu.brokeragent.BrokerAgent;
import com.mlaf.hu.communication.CommunicationAgent;
import com.mlaf.hu.communication.MailAgent;
import com.mlaf.hu.communication.SlackAgent;
import com.mlaf.hu.decisionagent.DecisionAgent;
import com.mlaf.hu.models.Plan;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServiceDiscoveryTest {

    @Test
    public void SD_DECISION_AGENT() {
        DecisionAgent da = new DecisionAgent() {
            @Override
            public void unregisterSensorAgentCallback(AID sensoragent) {

            }

            @Override
            public void storeReading(double value) {

            }

            @Override
            public void executePlanCallback(Plan plan) {

            }
        };
        ServiceDescription daServiceDescription = da.createServiceDescription();
        ServiceDescription sdServiceDescription = ServiceDiscovery.SD_DECISION_AGENT();
        assertEquals(daServiceDescription.getName(), sdServiceDescription.getName());
        assertEquals(daServiceDescription.getType(), sdServiceDescription.getType());
    }

    @Test
    public void SD_BROKER_AGENT() {
        BrokerAgent ba = new BrokerAgent();
        ServiceDescription daServiceDescription = ba.createServiceDescription();
        ServiceDescription sdServiceDescription = ServiceDiscovery.SD_BROKER_AGENT();
        assertEquals(daServiceDescription.getName(), sdServiceDescription.getName());
        assertEquals(daServiceDescription.getType(), sdServiceDescription.getType());
    }

    @Test
    public void SD_COMM_SLACK_AGENT() {
        CommunicationAgent sa = new SlackAgent();
        ServiceDescription saServiceDescription = sa.createServiceDescription();
        ServiceDescription sdServiceDescription = ServiceDiscovery.SD_COMM_SLACK_AGENT();
        assertEquals(saServiceDescription.getName(), sdServiceDescription.getName());
        assertEquals(saServiceDescription.getType(), sdServiceDescription.getType());
    }

    @Test
    public void SD_COMM_MAIL_AGENT() {
        CommunicationAgent ma = new MailAgent();
        ServiceDescription maServiceDescription = ma.createServiceDescription();
        ServiceDescription sdServiceDescription = ServiceDiscovery.SD_COMM_MAIL_AGENT();
        assertEquals(maServiceDescription.getName(), sdServiceDescription.getName());
        assertEquals(maServiceDescription.getType(), sdServiceDescription.getType());
    }
}