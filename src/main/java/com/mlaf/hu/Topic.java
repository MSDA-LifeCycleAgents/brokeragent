package com.mlaf.hu;

import jade.core.AID;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Topic {
    private AID topic;
    private ArrayList<AID> subscribers;
    private ArrayList<Message> messages;
    private int daysToKeepMessages;

    Topic(AID topic, int dTKM) {
        this.topic = topic;
        this.subscribers = new ArrayList<AID>();
        this.messages = new ArrayList<Message>();
        this.daysToKeepMessages = dTKM;
    }

    public ArrayList<AID> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(ArrayList<AID> subscribers) {
        this.subscribers = subscribers;
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
        } catch (IndexOutOfBoundsException IOB) {
            return null;
        }
    }

    public AID getTopic() {
        return topic;
    }

    public void setTopic(AID topic) {
        this.topic = topic;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    Message getLastMessage() {
        Message lastMessage = this.messages.get(this.messages.size() - 1);
        this.messages.remove(lastMessage);
        return lastMessage;
    }

    void removeOldMessages() {
        for (Message message: this.messages) {
            if (LocalDateTime.now().minusDays(this.daysToKeepMessages).equals(message.getDateOfArrival())) {
                this.messages.remove(message);
            }
        }
    }
}
