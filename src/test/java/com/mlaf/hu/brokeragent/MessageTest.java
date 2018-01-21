package com.mlaf.hu.brokeragent;

import com.mlaf.hu.models.Message;
import jade.core.AID;
import junit.framework.TestCase;

import java.time.LocalDateTime;

public class MessageTest extends TestCase {
    Message m;
    AID pub;
    LocalDateTime pubdate;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pub = new AID("Publisher", true);
        pubdate = LocalDateTime.now();
        m = new Message("Dit is een test berichtje!", pub, pubdate);
    }

    public void testGetContent() throws Exception {
        assertEquals("Dit is een test berichtje!", m.getContent());
    }

    public void testGetDateOfArrival() throws Exception {
        assertEquals(pubdate, m.getDateOfArrival());
    }

    public void testGetPublisher() throws Exception {
        assertEquals(pub, m.getPublisher());;
    }

    public void testToString() throws Exception {
        assertNotNull(m.toString());
    }

    public void testHashCode() {
        pub = new AID("Publisher", true);
        pubdate = LocalDateTime.now();
        Message x = new Message("Dit is een test berichtje!", pub, pubdate);
        Message y = new Message("Dit is een test berichtje!", pub, pubdate);
        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

}