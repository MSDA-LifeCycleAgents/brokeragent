/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlaf.hu.proxy.parser;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.Envelope;
import javax.xml.bind.JAXBException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Hans
 */
public class AclXmlParser {

    private static java.util.logging.Logger XmlParserLogger = Logger.getLogger("XmlParserLogger");

    public static String parseACLToXML(ACLMessage message) {
        StringWriter marshalledObject = new StringWriter();
        
        AclObject aclObject = new AclObject();
        ArrayList<Receiver> receiverList = new ArrayList<>();
        ArrayList<Sender> senderList = new ArrayList<>();

        Sender sender = newSender(message);
        Receiver receiver = newReceiver(message);
        
        receiverList.add(receiver);
        senderList.add(sender);
        
        aclObject.setPerformative("" + getPerformative(message.getPerformative()));
        aclObject.setSender(senderList);
        aclObject.setReceiver(receiverList);
        aclObject.setContent(message.getContent());
        aclObject.setLanguage(message.getLanguage());
        aclObject.setOntology(message.getOntology());
        aclObject.setProtocol(message.getProtocol());
        aclObject.setConversationId(message.getConversationId());

        try {
            JAXBContext context = JAXBContext.newInstance(AclObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING,"UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(aclObject, marshalledObject);
        } catch (JAXBException ex) {
            java.util.logging.Logger.getLogger(AclXmlParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        String xml = marshalledObject.toString().replace("&lt;", "<").replace("&gt;",">");
        return xml;
    }

    private static Receiver newReceiver(ACLMessage message) {
        Receiver receiver = new Receiver();
        Iterator receiverIterator = message.getAllReceiver();
        if(receiverIterator.hasNext()) {

            AID aid = (AID) message.getAllReceiver().next();

            ArrayList<String> adressesReceiver = new ArrayList<>();
            adressesReceiver.addAll(Arrays.asList(aid.getAddressesArray()));
            receiver.setName(aid.getName());
            receiver.setAddresses(adressesReceiver);
        }
        return receiver;
    }

    private static Sender newSender(ACLMessage message) {
        Sender sender = new Sender();
        sender.setName(message.getSender().getName());
        ArrayList<String> adressesSender = new ArrayList<>();
        adressesSender.addAll(Arrays.asList(message.getSender().getAddressesArray()));
        sender.setAdresses(adressesSender);
        return sender;
    }

    public static ACLMessage parse(String xml, Envelope envelope){
        ACLMessage message = parseBody(xml);
        message.setEnvelope(envelope);
        return message;
    }

    public static ACLMessage parseBody(String body) {
        
        AclObject aclObject = JAXB.unmarshal(new StringReader(body), AclObject.class);
//        Sender sender = JAXB.unmarshal(new StringReader(body), Sender.class);
//        Receiver receiver = JAXB.unmarshal(new StringReader(body), Receiver.class);
        return parseXMLToACL(aclObject);
    }

    private static ACLMessage parseXMLToACL(AclObject aclObject) {

        ACLMessage message = new ACLMessage(getPerformative(aclObject.getPerformative()));

        String senderName = aclObject.getSender().get(0).getName();

        AID senderAid = new AID();
        senderAid.setName(senderName);
        senderAid.addAddresses(senderName);
        message.setSender(senderAid);

        aclObject.getReceiver().forEach((receiver) -> {
            String receiverName = receiver.getName();
            String senderUrl = receiver.getAddresses().get(0);
            AID receiverAid = new AID();
            receiverAid.setName(receiverName);
            receiverAid.addAddresses(senderUrl);
            message.addReceiver(receiverAid);
        });

        String content = aclObject.getContent();
        String language = aclObject.getLanguage();
        String ontology = aclObject.getOntology();
        String protocol = aclObject.getProtocol();
        String conversationID = aclObject.getConversationId();

        message.setContent(content);
        message.setLanguage(language);
        message.setOntology(ontology);
        message.setProtocol(protocol);
        message.setConversationId(conversationID);

        return message;
    }

    private static int getPerformative(String performative) {
        String[] performatives = ACLMessage.getAllPerformativeNames();
        for (int i = 0; i < performatives.length; i++) {
            if (performatives[i].equals(performative.toUpperCase())) {
                return i;
            }
        }
        return -1;
    }

    private static String getPerformative(int performative) {
        return ACLMessage.getAllPerformativeNames()[performative];
    }

}
