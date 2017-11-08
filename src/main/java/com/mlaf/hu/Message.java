package com.mlaf.hu;

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


    public LocalDateTime getDateOfArrival() {
        return dateOfArrival;
    }

    public AID getPublisher() {
        return publisher;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message: ");
        sb.append(content);
        if (this.publisher != null) {
            sb.append("Publisher ");
            sb.append(publisher);
        }
        if (this.dateOfArrival != null) {
            sb.append("Date of Arrival ");
            sb.append(dateOfArrival);
        }
        return sb.toString();
    }
}
