package com.mlaf.hu.communication;

import com.mlaf.hu.loggeragent.LoggerAgentLogHandler;
import jade.domain.FIPAAgentManagement.ServiceDescription;


import com.mlaf.hu.helpers.Configuration;
import java.util.logging.Level;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackException;
import net.gpedro.integrations.slack.SlackMessage;

/**
 * @author Rogier
 */
public class SlackAgent extends CommunicationAgent {
    private static final java.util.logging.Logger logger = jade.util.Logger.getLogger(SlackAgent.class.getName());
    private static Configuration config = Configuration.getInstance();
    private static final boolean LOGGER_HANDLER = Boolean.parseBoolean(config.getProperty("slack.logger_handler"));

    public SlackAgent() {
        super();
        if (LOGGER_HANDLER) {
            logger.addHandler(new LoggerAgentLogHandler(this, 60));
        }
    }

    @Override
    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName("SlackAgent");
        sd.setType("SlackAgent");
        return sd;
    }
    
    @Override
    protected void send(String message, String channel) {
        String webhook_url = config.getProperty("slack.webhook_url");
        String messageTitle = config.getProperty("slack.message_title");
        
        try{
            SlackApi api = new SlackApi(webhook_url);

            if (channel == null)
                api.call(new SlackMessage(messageTitle, message));
            else
                api.call(new SlackMessage(channel, messageTitle, message));   
        }catch(SlackException e){
            logger.log(Level.WARNING, "Failed to send message: {0}", e.toString());
        }
    }
}
