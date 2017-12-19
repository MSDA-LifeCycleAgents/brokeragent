package com.mlaf.hu.decisionagent.representationmodels;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "plans")
@XmlAccessorType(XmlAccessType.FIELD)
public class Plans {
    @XmlElement(name = "plan")
    private ArrayList<Plan> plans = null;

    public ArrayList<Plan> getPlans() {
        return plans;
    }

    public void setPlans(ArrayList<Plan> plans) {
        this.plans = plans;
    }
}
