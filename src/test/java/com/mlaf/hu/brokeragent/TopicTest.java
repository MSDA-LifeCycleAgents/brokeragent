package com.mlaf.hu.brokeragent;

import com.mlaf.hu.models.Message;
import com.mlaf.hu.models.Topic;
import jade.core.AID;
import junit.framework.TestCase;

import java.time.LocalDateTime;

public class TopicTest extends TestCase {

    public void testAddSubsribers() throws Exception {
        Topic t = new Topic(new AID("Test-Topic", true), 1);
        AID s1 = new AID("test-sub-1", true);
        AID s2 = new AID("test-sub-2", true);
        AID s3 = new AID("test-sub-3", true);
        assertEquals(0, t.getSubscribers().size());
        t.addToSubscribers(s1);
        assertEquals(1, t.getSubscribers().size());
        assertNotNull(t.getSubscriber(s1));
        assertNull(t.getSubscriber(s2));
        t.addToSubscribers(s2);
        t.addToSubscribers(s3);
        assertNotNull(t.getSubscriber(s2));
        assertNotNull(t.getSubscriber(s3));
    }

    public void testGetSubscibersOtherObjectSameName() {
        Topic t = new Topic(new AID("Test-Topic", true), 1);
        AID s1 = new AID("test-sub-1", true);
        t.addToSubscribers(s1);
        assertNotNull(t.getSubscriber(s1));
    }

    public void testGetSubscriber() throws Exception {
        Topic t = new Topic(new AID("Test-Topic", true), 1);
        AID s1 = new AID("test-sub-1", true);
        AID s1_other = new AID("test-sub-1", true);
        t.addToSubscribers(s1);
        assertNotNull(t.getSubscriber(s1_other));
    }

    public void testGetJadeTopic() throws Exception {
        Topic t = new Topic(new AID("Test-Topic", true), 1);
        assertEquals(new AID("Test-Topic", true), t.getJadeTopic());
    }

    public void testGetLastMessageEmpty() {
        Topic t = new Topic();
        System.out.println(t.toString());
        assertEquals(null, t.getOldestMessage());
    }

    public void testGetOldestMessage() throws Exception {
        Topic t = new Topic(new AID("Test-Topic", true), 1);
        Message m1 = new Message("Hoi1");
        Message m2 = new Message("Hoi2");
        Message m3 = new Message("Hoi3");
        Message m4 = new Message("Hoi4");
        t.addToMessages(m1);
        t.addToMessages(m2);
        t.addToMessages(m3);
        t.addToMessages(m4);
        assertEquals(m1, t.getOldestMessage());
        assertEquals(m2, t.getOldestMessage());
        assertEquals(m3, t.getOldestMessage());
        assertEquals(m4, t.getOldestMessage());
    }

    public void testRemoveOldMessages() throws Exception {
        Topic t = new Topic(new AID("Test-Topic", true), 1);
        LocalDateTime oldDate = LocalDateTime.now().minusDays(t.getDaysToKeepMessages() + 1);
        Message old_Message = new Message("Oud Bericht", new AID("Pub", true), oldDate);
        assertEquals(0, t.getQueueSize());
        t.addToMessages(old_Message);
        assertEquals(1, t.getQueueSize());
        t.removeOldMessages();
        assertEquals(0, t.getQueueSize());
    }

    public void testHashCode() {
        Topic x = new Topic(new AID("Test-Topic", true), 1);
        Topic y = new Topic(new AID("Test-Topic", true), 1);
        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

}