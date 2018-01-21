package com.mlaf.hu.models;

import jade.core.AID;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Message implements Serializable {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        Message rsh = (Message) obj;
        return new EqualsBuilder()
                .append(content, rsh.content)
                .append(publisher, rsh.publisher)
                .append(dateOfArrival, rsh.dateOfArrival)
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(content)
                .append(publisher)
                .append(dateOfArrival)
                .toHashCode();
    }
}
