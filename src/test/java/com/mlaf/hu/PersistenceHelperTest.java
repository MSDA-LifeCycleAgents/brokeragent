package com.mlaf.hu;

import com.mlaf.hu.exceptions.TopicNotManagedException;
import jade.core.AID;
import junit.framework.TestCase;
import org.junit.Test;

public class PersistenceHelperTest extends TestCase {

    public void testStoreObject() {
        AID a = new AID("Test-Topic", true);
        Topic t = new Topic(a, 1);
        Message m1 = new Message("Hoi1");
        Message m2 = new Message("Hoi2");
        Message m3 = new Message("Hoi3");
        Message m4 = new Message("Hoi4");
        t.addToMessages(m1);
        t.addToMessages(m2);
        t.addToMessages(m3);
        t.addToMessages(m4);
        PersistenceHelper.storeObject(t, "Test-Topic");
        Topic loadedTopic = null;
        try {
            loadedTopic = PersistenceHelper.loadTopic("Test-Topic");
        } catch (TopicNotManagedException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(t, loadedTopic);
    }

    public void testLoadTopic() {
        try {
            PersistenceHelper.loadTopic("NonExisting1");
            fail();
        } catch (TopicNotManagedException ignored) {
        }
    }

}