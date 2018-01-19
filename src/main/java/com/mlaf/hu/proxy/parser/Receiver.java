/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlaf.hu.proxy.parser;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


/**
 *
 * @author Hans
 */
public class Receiver {

    private String name;
    private ArrayList<String> adresses;

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "adresses")
    @XmlElement(name = "url")
    public ArrayList<String> getAdresses() {
        return adresses;
    }

    public void setAdresses(ArrayList<String> adresses) {
        this.adresses = adresses;
    }
}
