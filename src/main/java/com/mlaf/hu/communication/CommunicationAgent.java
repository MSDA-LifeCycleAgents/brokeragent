package com.mlaf.hu.communication;

import com.mlaf.hu.helpers.DFServices;
import com.mlaf.hu.loggeragent.LoggerAgentLogHandler;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rogier
 */
public abstract class CommunicationAgent extends Agent{
    private static final Logger LOGGER = Logger.getLogger(CommunicationAgent.class.getName());

    public CommunicationAgent() {
        super();
    }
    
    @Override
    public void setup(){
        if (DFServices.registerAsService(createServiceDescription(), this)) {
            addBehaviour(
                    new CyclicBehaviour() {
                        @Override
                        public void action() {
                            ACLMessage aclMessage = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                            if (aclMessage != null) {
                                String message = aclMessage.getContent();
                                String to = aclMessage.getUserDefinedParameter("to");
                                if (message != null)
                                    send(message, to);
                                else
                                    LOGGER.log(Level.WARNING, "Failed to send message: invalid request: {0}", message);
                            } else {
                                block();
                            }
                        }
                    }
            );
        }
    }
    
    @Override
    @SuppressWarnings("UseSpecificCatch")
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ignore) {}
    }
    
    /**
     * Method returns the ServiceDescription that will be used to advertise
     * the communication agent in the DF.
     * 
     * @return User defined service description.
     */
    public abstract ServiceDescription createServiceDescription();
    
    /**
     * Method will be called upon an ACL request to the communication agent.
     * Specific handling will be implemented here for each communication agent.
     * 
     * @param message the content of the ACL message
     * @param to the ACL message user defined parameter "to". 
     *  Please note that "to" parameter is optional and should be dealt with 
     *  accordingly in method implementation.
     */
    protected abstract void send(String message, String to);
    
}
