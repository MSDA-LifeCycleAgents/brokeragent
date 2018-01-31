package com.mlaf.hu.models;

import jade.core.AID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement( name = "Topic" )
public class Topic implements Serializable {
    private AID jadeTopic;
    private ArrayList<AID> subscribers = new ArrayList<>();
    private ArrayList<Message> messages =  new ArrayList<>();
    private int daysToKeepMessages;
    private String topicName;

    public Topic() {}

    public Topic(int dTKM) {
        this.daysToKeepMessages = dTKM;
    }

    public Topic (String topicName) {
        this.topicName = topicName;
    }

    public Topic(AID jadeTopic, int dTKM) {
        this.jadeTopic = jadeTopic;
        this.subscribers = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.daysToKeepMessages = dTKM;
    }

    public Topic (AID jadeTopic, int dTKM, String topicName) {
        this.jadeTopic = jadeTopic;
        this.daysToKeepMessages = dTKM;
        this.topicName = topicName;
    }

    public List<AID> getSubscribers() {
        return subscribers;
    }

    public int getDaysToKeepMessages() {
        return daysToKeepMessages;
    }

    public int getQueueSize() {
        return this.messages.size();
    }

    public void addToSubscribers(AID subscriber) {
        this.subscribers.add(subscriber);
    }

    public void addToMessages(Message message) {
        this.messages.add(message);
    }

    public AID getSubscriber(AID subscriber) {
        int indexSubscriber = this.subscribers.indexOf(subscriber);
        try {
            return this.subscribers.get(indexSubscriber);
        } catch (IndexOutOfBoundsException iobException) {
            return null;
        }
    }

    public AID getJadeTopic() {
        return jadeTopic;
    }

    public void setJadeTopic(AID jadeTopic) {
        this.jadeTopic = jadeTopic;
    }

    public Message getOldestMessage() {
        try {
            Message lastMessage = this.messages.get(0);
            this.messages.remove(lastMessage);
            return lastMessage;
        }
        catch (NullPointerException | IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    public void removeOldMessages() {
        List<Message> messagesToRemove = new ArrayList<>();
        for (Message message: this.messages) {
            if (LocalDateTime.now().minusDays(this.daysToKeepMessages).isAfter(message.getDateOfArrival())) {
                messagesToRemove.add(message);
            }
        }
        this.messages.removeAll(messagesToRemove);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false;}
        if (obj == this) { return true;}
        if (obj.getClass() != getClass()) { return false; }
        Topic rsh = (Topic) obj;

        return new EqualsBuilder()
                .append(jadeTopic, rsh.jadeTopic)
                .append(subscribers, rsh.subscribers)
                .append(daysToKeepMessages, rsh.daysToKeepMessages)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(jadeTopic)
                .append(subscribers)
                .append(messages)
                .append(daysToKeepMessages)
                .toHashCode();
    }

    public String getTopicName() {
        return topicName;
    }

    @XmlElement( name = "name" )
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @XmlElement( name = "daysToKeepMessages" )
    public void setDaysToKeepMessages(int dTKM ) {
        this.daysToKeepMessages = dTKM;
    }

    public String toString() {
        String oldestMessage = "No messages";
        try {
            oldestMessage = this.messages.get(0).toString();
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
        return String.format("topicName: %s\ndaysToKeepMessages: %s\noldest message content: %s",
                this.topicName,
                this.daysToKeepMessages,
                oldestMessage
        );
    }
}
