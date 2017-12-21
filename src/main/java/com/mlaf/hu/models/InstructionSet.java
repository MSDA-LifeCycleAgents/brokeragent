package com.mlaf.hu.models;

import com.mlaf.hu.models.Fallback;
import com.mlaf.hu.models.Messaging;
import com.mlaf.hu.models.Sensors;
import org.springframework.scheduling.support.CronSequenceGenerator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.TimeZone;

@XmlRootElement( name = "instructions")
public class InstructionSet {
    private String identifier;
    private Messaging messaging;
    private Sensors sensors;
    private Fallback fallback;
    private String heartbeatTimePattern;
    private boolean active = true;
    private boolean notInteger = false;

    @XmlElement ( name = "identifier")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @XmlElement ( name = "messaging")
    public Messaging getMessaging() {
        return messaging;
    }

    public void setMessaging(Messaging messaging) {
        this.messaging = messaging;
    }

    @XmlElement ( name = "fallback")
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isNotInteger() {
        return this.notInteger;
    }

    public String testIntegrity() {
        String integer = "The XML is in correct format";
        String missing = "";
        if (this.identifier == null) {
            this.notInteger = true;
            missing += "<identifier></identifier>";
        }
        if (this.messaging.getTopic() == null && !this.messaging.isDirectToDecisionAgent()) {
            this.notInteger = true;
            missing += "<messaging>\n" +
                        "\t<topic>\n" +
                        "\t\t<name></name>\n" +
                        "\t\t<daysToKeepMessages></daysToKeepMessages>\n" +
                        "\t</topic>\n" +
                        "\t<directToDecisionAgent>false</directToDecisionAgent>\n" +
                        "</messaging>";
        }
        if (this.sensors.getSensors().get(0) == null) {
            this.notInteger = true;
            missing += "<sensors>\n" +
                        "\t<sensor id=\"\">\n" +
                        "\t\t<label></label>\n" +
                        "\t\t<min></min>\n" +
                        "\t\t<max></max>\n" +
                        "\t\t<unit></unit>\n" +
                        "\t\t<intervalinseconds></intervalinseconds>\n" +
                        "\t\t<plans>\n" +
                        "\t\t\t<plan>\n" +
                        "\t\t\t\t<below></below>\n" +
                        "\t\t\t\t<message></message>\n" +
                        "\t\t\t\t<via></via>\n" +
                        "\t\t\t\t<to></to>\n" +
                        "\t\t\t\t<limit></limit>\n" +
                        "\t\t\t</plan>\n" +
                        "\t\t</plans>\n" +
                        "\t\t<amountOfBackupMeasurements>20</amountOfBackupMeasurements>\n" +
                        "\t</sensor>\n" +
                        "</sensors>";
        }
        if (this.fallback == null) {
            this.notInteger = true;
            missing += "<fallback>\n" +
                        "\t<via>ScreenAgent</via>\n" +
                        "\t<to></to>\n" +
                        "</fallback>\n";
        }
        if (notInteger) {
            return String.format("XML is missing the following tag(s):\n%s", missing);
        }
        return integer;
    }

    @XmlElement
    public String getHeartbeatTimePattern() {
        return heartbeatTimePattern;
    }

    public void setHeartbeatTimePattern(String heartbeatTimePattern) {
        this.heartbeatTimePattern = heartbeatTimePattern;
    }

    public Date nextDate () {
        CronSequenceGenerator generator = new CronSequenceGenerator(this.heartbeatTimePattern, TimeZone.getDefault());
        return generator.next(new Date());
    }
}
