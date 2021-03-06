package com.mlaf.hu.models;

import java.io.Serializable;

public class Plan implements Serializable {
    private double below = 0;
    private double above = 1;
    private String message;
    private String via; //FIXME should be class
    private String to;
    private int limit;
    private int currentLimit;

    public Plan() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getBelow() {
        return below;
    }

    public void setBelow(double below) {
        this.below = below;
    }

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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public double getAbove() {
        return above;
    }

    public void setAbove(double above) {
        this.above = above;
    }

    public int getCurrentLimit() {
        return currentLimit;
    }

    public void setCurrentLimit(int currentLimit) {
        this.currentLimit = currentLimit;
    }
}
