package com.mlaf.hu.sensoragent;

import com.mlaf.hu.sensoragent.behavior.ReadSensorsBehavior;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;

public abstract class SensorAgent extends Agent{
    private ArrayList<Sensor> sensors = new ArrayList<>();

    public SensorAgent() {
        addBehaviour(new ReadSensorsBehavior(this, 1000));
        //TODO(Auke) Get tickrate from instructionset.
        //TODO(Auke) Look into sudden stopping of application and restarting it.

    }

    public ArrayList<Sensor> getSensors() {
        return new ArrayList<Sensor>(sensors);
    }


    public abstract String getInstructionXML();

    public void addSensor(Sensor sensor) {
        // TODO(Auke) Check if sensor ID is unique!
        sensors.add(sensor);
    }

    public void sendSensorReadings(ArrayList<SensorReading> readings) {
        String XML = sensorReadingsToXML(readings);

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(getDecisionAgent());
        msg.setLanguage("XML");
        msg.setOntology("MLAF-Sensor-XML");
        msg.setContent(XML);
        //TODO(Auke) Do we want to send XML? Maybe send Readings since sending of serializable is also possible?

    }

    private AID getDecisionAgent() {
        // Maybe rename to getDestination? Since sending to topic should also be possible.
        //TODO Add getting of BrokerAgent AID
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("sensor-reading"); // TODO Maybe another type name?
        sd.addOntologies("MLAF-Sensor-XML");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search( this, template);
            //TODO Catch 0 result
            //TODO Handle more than one
            return result[0].getName();

        } catch (FIPAException e) {
            //TODO Handle
            e.printStackTrace();
            return null;
        }


    }

    private static String sensorReadingsToXML(ArrayList<SensorReading> readings) {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            //TODO Do we want an actual namespace document?
            //TODO Add readings timestamp
            Element mainRootElement = doc.createElementNS("https://github.com/MSDA-LifeCycleAgents/SensorReadingsXML", "readings");
            doc.appendChild(mainRootElement);

            for (SensorReading reading: readings) {
                mainRootElement.appendChild(reading.getReadingNode(doc));

            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            transformer.transform(source, new StreamResult(stringWriter));
            return stringWriter.toString();

        } catch (Exception e) {
            //FIXME
            e.printStackTrace();
            return null;
        }
    }


}
