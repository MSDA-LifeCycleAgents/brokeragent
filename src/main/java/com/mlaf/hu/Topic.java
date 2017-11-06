package com.mlaf.hu;

import jade.core.AID;

import java.util.ArrayList;

public class Topic {
    private AID topic;
    private ArrayList<AID> subscribers;
    private ArrayList<Message> messages;

    Topic(AID topic) {
        this.topic = topic;
        this.subscribers = new ArrayList<AID>();
        this.messages = new ArrayList<Message>();
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
        return this.messages.get(this.messages.size() - 1);
    }
}
