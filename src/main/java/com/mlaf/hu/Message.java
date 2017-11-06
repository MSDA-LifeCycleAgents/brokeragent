package com.mlaf.hu;

import jade.core.AID;

public class Message {
    private String content;
    private AID publisher;

    public Message(String msgContent) {
        this.content = msgContent;
    }
    public Message(String msgContent, AID publisher) {
        this.content = msgContent;
        this.publisher = publisher;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
