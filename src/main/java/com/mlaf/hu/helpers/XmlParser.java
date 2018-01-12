/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlaf.hu.helpers;

import com.mlaf.hu.helpers.exceptions.ParseException;
import jade.util.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Rogier
 */
public class XmlParser {
    private static java.util.logging.Logger XmlParserLogger = Logger.getLogger("XmlParserLogger");

    public static Document loadXMLFromString(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public static String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return null;
    }

    public static <T> T parseToObject(Class<T> clazz, String xml) throws ParseException {
        try {
            Object obj = JAXB.unmarshal(new StringReader(xml), clazz);
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new ParseException(String.format("Error parsing to object: %s", e.getMessage()));
        }
    }

    public static String parseToXml(Object obj) throws ParseException {
        StringWriter marshalledObject = new StringWriter();
        try {
            JAXB.marshal(obj, marshalledObject);
            return marshalledObject.toString();
        } catch (Exception e) {
            throw new ParseException(String.format("Error parsing to XML: %s", e.getMessage()));
        }
    }

}
