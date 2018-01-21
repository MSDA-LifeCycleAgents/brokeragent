package com.mlaf.hu.models;

import com.mlaf.hu.helpers.exceptions.SensorNotFoundException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDateTime;

@XmlRootElement(name = "instructions")
public class InstructionSet implements Serializable {
    private String identifier;
    private Messaging messaging;
    private Sensors sensors;
    private Fallback fallback;
    private boolean active = true;
    private boolean mallformed = false;
    private LocalDateTime lastReceivedDataPackageAt;
    private int amountOfMissedDataPackages = 5;
    private LocalDateTime registeredAt;

    @XmlElement(name = "identifier")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @XmlElement(name = "messaging")
    public Messaging getMessaging() {
        return messaging;
    }

    public void setMessaging(Messaging messaging) {
        this.messaging = messaging;
    }

    @XmlElement(name = "fallback")
    public Fallback getFallback() {
        return fallback;
    }

    public void setFallback(Fallback fallback) {
        this.fallback = fallback;
    }

    public Sensors getSensors() {
        return sensors;
    }

    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
    }

    public boolean isActive() {
        return active;
    }

    public void setInActive() {
        this.active = false;
    }
    public void setActive() {
        this.active = true;
    }

    public boolean isMallformed() {
        return this.mallformed;
    }

    public String checkComposition() {
        String missing = "";
        if (this.identifier == null) {
            this.mallformed = true;
            missing += "<identifier></identifier>";
        }
        if (this.messaging.getTopic() == null && !this.messaging.isDirectToDecisionAgent()) {
            this.mallformed = true;
            missing += "<messaging>\n" +
                        "\t<topic>\n" +
                        "\t\t<name></name>\n" +
                        "\t\t<daysToKeepMessages></daysToKeepMessages>\n" +
                        "\t</topic>\n" +
                        "\t<directToDecisionAgent>false</directToDecisionAgent>\n" +
                        "</messaging>";
        }
        if (this.sensors.getSensors().get(0) == null) {
            this.mallformed = true;
            missing += "<sensors>\n" +
                        "\t<sensor id=\"\">\n" +
                        "\t\t<label></label>\n" +
                        "\t\t<intervalinseconds></intervalinseconds>\n" +
                        "\t\t<unit></unit>\n" +
                        "\t\t<measurements>\n" +
                        "\t\t\t<measurement id=\"\">\n" +
                        "\t\t\t\t<min></min>\n" +
                        "\t\t\t\t<max></max>\n" +
                        "\t\t\t\t<plans>\n" +
                        "\t\t\t\t\t<plan>\n" +
                        "\t\t\t\t\t\t<below></below>\n" +
                        "\t\t\t\t\t\t<above></above>\n" +
                        "\t\t\t\t\t\t<message></message>\n" +
                        "\t\t\t\t\t\t<via></via>\n" +
                        "\t\t\t\t\t\t<to></to>\n" +
                        "\t\t\t\t\t\t<limit></limit>\n" +
                        "\t\t\t\t\t</plan>\n" +
                        "\t\t\t\t</plans>\n" +
                        "\t\t\t</measurement>\n" +
                        "\t\t</measurements>\n" +
                        "\t</sensor>\n" +
                        "</sensors>";
        }
        if (this.fallback == null) {
            this.mallformed = true;
            missing += "<fallback>\n" +
                        "\t<via>ScreenAgent</via>\n" +
                        "\t<to></to>\n" +
                        "</fallback>\n";
        }
        if (mallformed) {
            return String.format("XML is missing the following tag(s):\n%s", missing);
        }
        return null;
    }

    public LocalDateTime getLastReceivedDataPackageAt() {
        return lastReceivedDataPackageAt;
    }

    public void setLastReceivedDataPackageAt(LocalDateTime lastReceivedDataPackageAt) {
        this.lastReceivedDataPackageAt = lastReceivedDataPackageAt;
    }

    @XmlElement(name = "amountOfMissedDataPackages")
    public int getAmountOfMissedDataPackages() {
        return amountOfMissedDataPackages;
    }

    public void setAmountOfMissedDataPackages(int amountOfMissedDataPackages) {
        this.amountOfMissedDataPackages = amountOfMissedDataPackages;
    }

    public int getHighestIntervalFromSensors() {
        int highestInterval = 0;
        for(Sensor s : this.sensors.getSensors()) {
            if (s.getIntervalInSeconds() > highestInterval) {
                highestInterval = s.getIntervalInSeconds();
            }
        }
        return highestInterval;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

     public Sensor getSensor(String sensorId) throws SensorNotFoundException {
        for(Sensor sensor: sensors.getSensors()) {
            if (sensor.getId().equals(sensorId)) {
                return sensor;
            }
        }
        throw new SensorNotFoundException(sensorId);
    }
}
