package com.mlaf.hu.helpers;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.util.Logger;

public class JadeServices {
    private static java.util.logging.Logger DFHelperLogger = Logger.getLogger("DFHelperLogger");

    public static void registerAsService(String name, String type, String ontology, String language, Agent a) {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(a.getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(name);
            sd.setType(type);
            if (ontology == null) {
                ontology = String.format("%s-ontology", type);
            }
            sd.addOntologies(ontology);
            if (language == null){
                language = FIPANames.ContentLanguage.FIPA_SL;
            }
            sd.addLanguages(language);
            dfd.addServices(sd);
            DFService.register(a, dfd);
            DFHelperLogger.log(Logger.INFO, String.format("Registered the %s as a service to the DF.", name));
        } catch (FIPAException e) {
            DFHelperLogger.log(Logger.SEVERE, () -> String.format("Registering as service failed: %s", e.getMessage()));
        }
    }

    public static AID getService(String serviceName, Agent currentAgent) { //FIXME nullpointer exception because DA.getDefaultDF() returns null. Why?
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(currentAgent, dfd);
            if (result.length > 0)
                return result[0].getName();
            else {
                DFHelperLogger.log(Logger.SEVERE, String.format("Could not get %s as a service, is it running?", serviceName));
            }
        } catch (FIPAException fe) {
            DFHelperLogger.log(Logger.SEVERE, () -> String.format("Something went wrong trying to find the %s Service: %s", serviceName, fe.getMessage()));
        }
        return null;
    }
}
