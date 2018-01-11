package com.mlaf.hu.helpers.schema;

import com.mlaf.hu.models.InstructionSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;

public class RunnerClass {
     public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(InstructionSet.class); // Change this to the desired class for a schema.xsd
        SchemaOutputResolver sor = new JAXBSchemaHelper();
        jaxbContext.generateSchema(sor);
    }
}
