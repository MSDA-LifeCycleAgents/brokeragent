package com.mlaf.hu.communication;

import com.mlaf.hu.loggeragent.LoggerAgentLogHandler;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import com.mlaf.hu.helpers.Configuration;
import jade.util.Logger;

/**
 *
 * @author Rogier
 */
public class MailAgent extends CommunicationAgent{
    private static final java.util.logging.Logger logger = Logger.getLogger(MailAgent.class.getName());
    private static Configuration config = Configuration.getInstance();
    private static final boolean LOGGER_HANDLER = Boolean.parseBoolean(config.getProperty("mailagent.logger_handler"));

    public MailAgent() {
        super();
        if (LOGGER_HANDLER) {
            logger.addHandler(new LoggerAgentLogHandler(this, 60));
        }
    }

    @Override
    public ServiceDescription createServiceDescription() {
        ServiceDescription sd = new ServiceDescription();
        sd.setName("MailAgent");
        sd.setType("MailAgent");
        return sd;
    }
    
    @Override
    protected void send(String message, String to){
        Configuration config = Configuration.getInstance();
        String host = config.getProperty("mail.host");
        String from = config.getProperty("mail.from");
        String password = config.getProperty("mail.password");
        String port = config.getProperty("mail.port");
        
        if(host == null || from == null){
            logger.log(Level.WARNING, "Failed to send message: Mail configuration not present");
            return;
        }
        
        String subject = config.getProperty("mail.default_subject");
        
        Properties sysProps = System.getProperties();
        sysProps.setProperty("mail.smtp.auth", "true");
	    sysProps.setProperty("mail.smtp.starttls.enable", "true");
        sysProps.setProperty("mail.smtp.host", host);
        sysProps.setProperty("mail.smtp.port", port);

        Session session = Session.getInstance(sysProps, new Authenticator(){
            @Override
            protected PasswordAuthentication  getPasswordAuthentication(){
                return new PasswordAuthentication(from, password);
            }
        });
        
        try{
            MimeMessage mime = new MimeMessage(session);
            mime.setFrom(new InternetAddress(from));
            mime.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mime.setSubject(subject);
            mime.setText(message);
            Transport.send(mime);
            logger.log(Logger.INFO, "Send e-mail to " + to + " with message:\n" + message);
        }catch(MessagingException e){
            logger.log(Level.WARNING, "Failed to send message: {0}", e.toString());
        }
    }
}
