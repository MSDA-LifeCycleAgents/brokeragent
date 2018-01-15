package com.mlaf.hu.helpers;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.Logger;

import java.util.ArrayList;
import java.util.logging.Level;

public class ServiceDiscovery {
    public static java.util.logging.Logger serviceDiscoveryLogger = Logger.getLogger("ServiceDiscoveryLogger");
    Agent agent;

    public ServiceDiscovery(Agent agent) {
        this.agent = agent;
    }

    public ArrayList<AID> lookupByDescriptor(ServiceDescription sd) {
        DFAgentDescription template = new DFAgentDescription();
        template.addServices(sd);
        return search(template);
    }

    private ArrayList<AID> search(DFAgentDescription template) {
        ArrayList<AID> resultArray = new ArrayList<>();
        try {
            DFAgentDescription[] result = DFService.search(agent, template);

            for (DFAgentDescription r: result) {
                resultArray.add(r.getName());
            }
        } catch (FIPAException e) {
            serviceDiscoveryLogger.log(Level.INFO, "Could not search Director Facilitator", e);
        }
        return resultArray;
    }

    public AID lookupDecisionAgent() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("decision-agent");
        sd.setName("DECISION-AGENT");
        ArrayList<AID> result = lookupByDescriptor(sd);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
}
