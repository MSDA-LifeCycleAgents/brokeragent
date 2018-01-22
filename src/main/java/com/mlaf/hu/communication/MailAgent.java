package com.mlaf.hu.communication;

import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import com.mlaf.hu.helpers.Configuration;

/**
 *
 * @author Rogier
 */
public class MailAgent extends CommunicationAgent{
    private static final Logger logger = Logger.getLogger(MailAgent.class.getName());
    
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
        }catch(MessagingException e){
            logger.log(Level.WARNING, "Failed to send message: {0}", e.toString());
        }
    }
}
