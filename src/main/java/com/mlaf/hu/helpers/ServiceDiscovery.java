package com.mlaf.hu.helpers;

import com.mlaf.hu.helpers.exceptions.ServiceDiscoveryNotFoundException;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

public class ServiceDiscovery {
    public static java.util.logging.Logger serviceDiscoveryLogger = Logger.getLogger("ServiceDiscoveryLogger");
    private Agent agent;
    private ServiceDescription serviceDescription = null;
    private LocalDateTime lastUpdate;
    private AID cachedAID;

    public ServiceDiscovery(Agent agent, ServiceDescription sd) {
        this.agent = agent;
        this.serviceDescription = sd;
    }

    public AID getAID() throws ServiceDiscoveryNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        if (cachedAID != null && now.isBefore(lastUpdate.plusMinutes(1))) {
            return cachedAID;
        }
        ArrayList<AID> result = lookupByDescriptor(this.serviceDescription);
        if (result.isEmpty()) {
            throw new ServiceDiscoveryNotFoundException("Could not find the service: " + this.serviceDescription.getName());
        }
        lastUpdate = LocalDateTime.now();
        cachedAID = result.get(0);
        return cachedAID;
    }

    public AID ensureAID(int timeout_s) throws ServiceDiscoveryNotFoundException {
        LocalDateTime timeout = LocalDateTime.now().plusSeconds(timeout_s);
        AID result;
        while (LocalDateTime.now().isBefore(timeout)) {
            result = getAID();
            if (result != null) {
                return result;
            }
        }
        throw new ServiceDiscoveryNotFoundException("Could not find the service: " + this.serviceDescription.getName());
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

    public static ServiceDescription SD_DECISION_AGENT() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("decision-agent");
        sd.setName("DECISION-AGENT");
        return sd;
    }

    public static ServiceDescription SD_BROKER_AGENT() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("message-broker");
        sd.setName("BROKER");
        return sd;
    }

    public static ServiceDescription SD_COMM_SLACK_AGENT() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("SlackAgent");
        sd.setName("SlackAgent");
        return sd;
    }

    public static ServiceDescription SD_COMM_MAIL_AGENT() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("MailAgent");
        sd.setName("MailAgent");
        return sd;
    }
}
