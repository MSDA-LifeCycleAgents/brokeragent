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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Hans
 */
public class AclXmlParser {

    private static String cutContent = "";
    private static java.util.logging.Logger XmlParserLogger = Logger.getLogger("XmlParserLogger");

    public static String parseACLToXML(ACLMessage message) {
        StringWriter marshalledObject = new StringWriter();

        AclObject aclObject = new AclObject();
        ArrayList<AidObject> receiverList = new ArrayList<>();
        ArrayList<AidObject> senderList = new ArrayList<>();
        ArrayList<AidObject> replyToList = new ArrayList<>();
        AidObject sender = newSender(message);
        AidObject receiver = newReceiver(message);
        AidObject replyTo = newReplyTo(message);

        receiverList.add(receiver);
        senderList.add(sender);
        replyToList.add(replyTo);

        aclObject.setPerformative("" + getPerformative(message.getPerformative()));
        aclObject.setSender(senderList);
        aclObject.setReplyTo(replyToList);
        aclObject.setReceiver(receiverList);
        aclObject.setContent(message.getContent());
        aclObject.setLanguage(message.getLanguage());
        aclObject.setOntology(message.getOntology());
        aclObject.setProtocol(message.getProtocol());
        aclObject.setConversationId(message.getConversationId());

        try {
            JAXBContext context = JAXBContext.newInstance(AclObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            marshaller.marshal(aclObject, marshalledObject);
        } catch (JAXBException ex) {
            java.util.logging.Logger.getLogger(AclXmlParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        String xml = marshalledObject.toString().replace("&lt;", "<").replace("&gt;", ">");
        return xml;
    }

    private static AidObject newReceiver(ACLMessage message) {
        AidObject receiver = new AidObject();
        Iterator receiverIterator = message.getAllReceiver();
        if (receiverIterator.hasNext()) {

            AID aid = (AID) message.getAllReceiver().next();

            ArrayList<String> addressesReceiver = new ArrayList<>();
            addressesReceiver.addAll(Arrays.asList(aid.getAddressesArray()));
            receiver.setName(aid.getName());
            receiver.setAddresses(addressesReceiver);
        }
        return receiver;
    }

    private static AidObject newSender(ACLMessage message) {
        AidObject sender = new AidObject();
        sender.setName(message.getSender().getName());
        ArrayList<String> addressesSender = new ArrayList<>();
        addressesSender.addAll(Arrays.asList(message.getSender().getAddressesArray()));
        sender.setAddresses(addressesSender);
        return sender;
    }

    private static AidObject newReplyTo(ACLMessage message) {
        AidObject replyTo = new AidObject();
        Iterator receiverIterator = message.getAllReplyTo();
        if (receiverIterator.hasNext()) {

            AID aid = (AID) message.getAllReplyTo().next();

            ArrayList<String> adressesReplyTo = new ArrayList<>();
            adressesReplyTo.addAll(Arrays.asList(aid.getAddressesArray()));
            replyTo.setName(aid.getName());
            replyTo.setAddresses(adressesReplyTo);
        }
        return replyTo;
    }

    public static ACLMessage parse(String xml, Envelope envelope) {

        ACLMessage message = parseBody(xml);
        message.setEnvelope(envelope);
        return message;
    }

    public static ACLMessage parseBody(String body) {
        String xmlWithReplacedContent = cutContent(body);
        AclObject aclObject = JAXB.unmarshal(new StringReader(xmlWithReplacedContent), AclObject.class);
        aclObject.setContent(cutContent);
        return parseXMLToACL(aclObject);
    }

    private static ACLMessage parseXMLToACL(AclObject aclObject) {

        ACLMessage message = new ACLMessage(getPerformative(aclObject.getPerformative()));

        String senderName = aclObject.getSender().get(0).getName();
        ArrayList<String> senderUrl = aclObject.getSender().get(0).getAddresses();

        AID senderAid = new AID();
        senderAid.setName(senderName);
        senderAid.addAddresses(senderUrl.get(0));
        message.setSender(senderAid);

        aclObject.getReceiver().forEach((receiver) -> {
            String receiverName = receiver.getName();
            String receiverUrl = receiver.getAddresses().get(0);
            AID receiverAid = new AID();
            receiverAid.setName(receiverName);
            receiverAid.addAddresses(receiverUrl);
            message.addReceiver(receiverAid);
        });

        aclObject.getReplyTo().forEach((replyTo) -> {
            String replyToName = replyTo.getName();
            String replyToUrl = replyTo.getAddresses().get(0);
            AID replyToAid = new AID();
            replyToAid.setName(replyToName);
            replyToAid.addAddresses(replyToUrl);
            message.addReplyTo(replyToAid);
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

    private static String cutContent(String xml) {
        Pattern contentPattern = Pattern.compile("(?<=<content>)([\\S\\s]*)(?=</content>)");
        Matcher m = contentPattern.matcher(xml);
        
        if (m.find()) {
            cutContent = m.group(1);
        }
        
        return xml.replace(cutContent, "");
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
