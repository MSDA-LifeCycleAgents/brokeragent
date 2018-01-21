package com.mlaf.hu.models;

import java.io.Serializable;

public class Fallback implements Serializable{
    private String via;
    private String to;

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
