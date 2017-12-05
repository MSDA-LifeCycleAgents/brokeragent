package com.mlaf.hu;

import jade.core.AID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Topic implements Serializable {
    private AID jadeTopic;
    private ArrayList<AID> subscribers;
    private ArrayList<Message> messages;
    private int daysToKeepMessages;

    Topic(AID jadeTopic, int dTKM) {
        this.jadeTopic = jadeTopic;
        this.subscribers = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.daysToKeepMessages = dTKM;
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

    void addToSubscribers(AID subscriber) {
        this.subscribers.add(subscriber);
    }

    void addToMessages(Message message) {
        this.messages.add(message);
    }

    AID getSubscriber(AID subscriber) {
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

    Message getOldestMessage() {
        try {
            Message lastMessage = this.messages.get(0);
            this.messages.remove(lastMessage);
            return lastMessage;
        }
        catch (IndexOutOfBoundsException ignored) {
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
//                .appendSuper(super.equals(obj)) //FIXME
                .append(jadeTopic, rsh.jadeTopic)
                .append(subscribers, rsh.subscribers)
//                .append(messages, rsh.messages) //FIXME
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
}
