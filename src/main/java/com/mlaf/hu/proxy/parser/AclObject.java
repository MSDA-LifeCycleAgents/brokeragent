/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlaf.hu.proxy.parser;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hans
 * <sender>
 * <agent-identifier> list
 * <name>>See-Sharp-Agent</name>
 * <adresses> list
 * <url>tcp://192.168.178.14:1234</url>
 * </adresses>
 * </agent-identifier>
 * </sender>
 */
@XmlRootElement(name = "fipa-message")
public class AclObject {

    private String performative;
    private ArrayList<Sender> sender;
    private ArrayList<Receiver> receiver;
    private String content;
    private String language;
    private String ontology;
    private String protocol;
    private String conversationId;

    @XmlAttribute(name = "communicative-act")
    public String getPerformative() {
        return performative;
    }

    public void setPerformative(String performative) {
        this.performative = performative;
    }

    @XmlElementWrapper(name = "sender")
    @XmlElement(name = "agent-identifier")
    public ArrayList<Sender> getSender() {
        return sender;
    }

    public void setSender(ArrayList<Sender> sender) {
        this.sender = sender;
    }

    @XmlElementWrapper(name = "receiver")
    @XmlElement(name = "agent-identifier")
    public ArrayList<Receiver> getReceiver() {
        return receiver;
    }

    public void setReceiver(ArrayList<Receiver> receiver) {
        this.receiver = receiver;
    }

    @XmlElement
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @XmlElement
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @XmlElement
    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    @XmlElement
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @XmlElement(name = "conversation-id")
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
