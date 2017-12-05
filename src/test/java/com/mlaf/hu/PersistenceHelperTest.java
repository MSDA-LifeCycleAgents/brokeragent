package com.mlaf.hu;

import com.mlaf.hu.exceptions.TopicNotManagedException;
import jade.core.AID;
import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PersistenceHelperTest extends TestCase {
    String testBrokenFilePath = PersistenceHelper.getBasePath() + "broken.ser";

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

    public void testLoadBrokenFile() {
        try {
            Topic failed = PersistenceHelper.loadTopic("broken");
            assertNull(failed);
        } catch (TopicNotManagedException ignored) {
            fail();
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(testBrokenFilePath))) {
            String content = "BROKENFILE BROKENFILE";
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        File file = new File(testBrokenFilePath);
        if (!file.delete()) {
            System.out.println("Could not delete test file " + testBrokenFilePath);
        }

    }
}