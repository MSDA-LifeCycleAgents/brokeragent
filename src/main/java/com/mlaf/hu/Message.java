package com.mlaf.hu;

import FIPA.DateTime;
import jade.core.AID;

import java.time.LocalDateTime;

public class Message {
    private String content;
    private AID publisher;
    private LocalDateTime dateOfArrival;

    public Message(String msgContent) {
        this.content = msgContent;
    }
    public Message(String msgContent, AID pub, LocalDateTime dOA) {
        this.content = msgContent;
        this.publisher = pub;
        this.dateOfArrival = dOA;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateOfArrival() {
        return dateOfArrival;
    }

    public void setDateOfArrival(LocalDateTime dateOfArrival) {
        this.dateOfArrival = dateOfArrival;
    }

    public AID getPublisher() {
        return publisher;
    }

    public void setPublisher(AID publisher) {
        this.publisher = publisher;
    }
}
