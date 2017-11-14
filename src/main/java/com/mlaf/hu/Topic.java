package com.mlaf.hu;

import jade.core.AID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Topic {
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
            Message lastMessage = this.messages.get(this.messages.size() - 1);
            this.messages.remove(lastMessage);
            return lastMessage;
        }
        catch (ArrayIndexOutOfBoundsException ignored) {
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
}
