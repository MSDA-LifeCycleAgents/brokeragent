/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlaf.hu.proxy.parser;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.Iterator;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Rogier
 */
public class XmlParserTest extends TestCase {
    private static final String XML = "<?xml version=\"1.0\"?>\n" +
            "<fipa-message communicative-act=\"request\">\n" +
            "	<sender><agent-identifier><name>See-Sharp-Agent</name><addresses><url>tcp://192.168.178.14:1234</url></addresses></agent-identifier></sender>\n" +
            "	<receiver><agent-identifier><name>MailAgent@192.168.178.14:1099/JADE</name><addresses><url>tcp://192.168.178.14:1099</url></addresses></agent-identifier></receiver>\n" +
            "	<content>\n" +
            "		<message>\n" +
            "			<content>Insert e-mail body here</content>\n" +
            "			<subject>Insert subject here</subject>	\n" +
            "			<to>receiver@email.com</to>\n" +
            "		</message>\n" +
            "	</content>\n" +
            "	<language>fipa-sl0</language>\n" +
            "	<ontology>fipa-agent-management</ontology>\n" +
            "	<protocol>fipa-request</protocol>\n" +
            "	<conversation-id>17</conversation-id>\n" +
            "</fipa-message>";
    
    // This method should return the exact same acl message as XML above does
    private ACLMessage createTestMessage(){
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        
        AID sender = new AID();
        sender.setName("See-Sharp-Agent");
        sender.addAddresses("tcp://192.168.178.14:1234");
        
        AID receiver = new AID();
        receiver.setName("MailAgent@192.168.178.14:1099/JADE");
        receiver.addAddresses("tcp://192.168.178.14:1099");
        
        message.setContent("<message>\n" +
            "			<content>Insert e-mail body here</content>\n" +
            "			<subject>Insert subject here</subject>	\n" +
            "			<to>receiver@email.com</to>\n" +
            "		</message>\n");
        
        message.setSender(sender);
        message.addReceiver(receiver);
        
        message.setLanguage("fipa-s10");
        message.setOntology("fipa-agent-management");
        message.setProtocol("fipa-request");
        message.setConversationId("17");
        
        return message;
    }
    
    @Test
    public void testXmlToAcl(){
        ACLMessage message = AclXmlParser.parseBody(XML);
        
        assertNotNull(message.getPerformative());

        assertNotNull(message.getSender());
        assertNotNull(message.getSender().getName());
        assertEquals(message.getSender().getAddressesArray().length, 1);

        int numberOfReceivers = 0;
        Iterator receiverIt = message.getAllReceiver();

        while (receiverIt.hasNext()) {
            AID receiver = (AID) receiverIt.next();
            assertNotNull(receiver.getName());
            assertEquals(receiver.getAddressesArray().length, 1);
            numberOfReceivers++;
        }
        assertEquals(numberOfReceivers, 1);

        assertNotNull(message.getContent());
        assertNotNull(message.getLanguage());
        assertNotNull(message.getOntology());
        assertNotNull(message.getProtocol());
        assertNotNull(message.getConversationId());
    }
    
    @Test
    public void testAclToXml(){
        String xml = AclXmlParser.parseACLToXML(createTestMessage());
        assertEquals(xml, XML);
    }
}
