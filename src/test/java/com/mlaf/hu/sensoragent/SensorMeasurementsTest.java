package com.mlaf.hu.sensoragent;

import junit.framework.TestCase;

public class SensorMeasurementsTest extends TestCase {

    public void testSensorMeasurements() {
        SensorMeasurements sm = new SensorMeasurements("Foo", "Bar");
        assertEquals("Foo", sm.getMeassurement());
        assertEquals("Bar", sm.getValue());
    }
}