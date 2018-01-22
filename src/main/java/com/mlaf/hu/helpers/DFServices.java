package com.mlaf.hu.helpers;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.util.Logger;

import java.util.concurrent.TimeUnit;

public class DFServices {
    private static java.util.logging.Logger DFHelperLogger = Logger.getLogger("DFHelperLogger");

    public static boolean registerAsService(ServiceDescription sd, Agent a) {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(a.getAID());
            dfd.addServices(sd);
            DFService.register(a, dfd);
            DFHelperLogger.log(Logger.INFO, String.format("Registered the %s as a service to the DF.", sd.getName()));
            return true;
        } catch (FIPAException e) {
            DFHelperLogger.log(Logger.SEVERE, () -> String.format("Registering as service failed: %s", e.getMessage()));
            return false;
        }
    }

    public static AID getService(String serviceType, Agent currentAgent) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(currentAgent, dfd);
            if (result.length > 0)
                return result[0].getName();
            else {
                DFHelperLogger.log(Logger.SEVERE, String.format("Could not get %s as a service, is it running?", serviceType));
            }
        } catch (FIPAException fe) {
            DFHelperLogger.log(Logger.SEVERE, () -> String.format("Something went wrong trying to find the %s Service: %s", serviceType, fe.getMessage()));
        }
        return null;
    }
}
