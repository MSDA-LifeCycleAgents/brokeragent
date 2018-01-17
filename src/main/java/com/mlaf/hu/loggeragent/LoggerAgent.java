package com.mlaf.hu.loggeragent;

import com.mlaf.hu.helpers.DFServices;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;


public class LoggerAgent extends Agent {
    private static final String SERVICE_NAME = "LOGGER-AGENT";
    private java.util.logging.Logger incomingLogger;

    public LoggerAgent() {
        super();
        DFServices.registerAsService(createServiceDescription(), this);
        incomingLogger = Logger.getLogger("com.mlaf.hu.loggeragent.central");
    }

    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName(SERVICE_NAME);
        sd.setType("logger-agent");
        return sd;
    }

    public static Object deserializeObject(String s) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
//            ObjectOutputStream ois = new ObjectOutputStream(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
