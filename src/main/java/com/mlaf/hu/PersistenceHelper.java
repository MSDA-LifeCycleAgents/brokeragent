package com.mlaf.hu;

import com.mlaf.hu.exceptions.TopicNotManagedException;
import jade.util.Logger;

import java.io.*;

public class PersistenceHelper {

    public static String getBasePath() {
        return "C:/BrokerAgent/";
    }

    public static void storeObject(Object obj, String name) {
        //TODO configurable storagepath

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getBasePath() + name + ".ser"))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            //TODO LOG
            e.printStackTrace();
//            brokerAgentLogger.log(Logger.SEVERE, () -> String.format("Could not write topic %s to disk %nError %s" , topicAID.getName(), e));
        }
    }

    public static Topic loadTopic(String name) throws TopicNotManagedException {
        Topic topic = null;
        try (ObjectInputStream ois  = new ObjectInputStream(new FileInputStream(getBasePath() + name + ".ser"))) {
            topic = (Topic) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new TopicNotManagedException("Topic " + name + " is not found on disk");
        } catch (IOException | ClassNotFoundException e) {
            // Something out of our hands has gone wrong?
            //TODO LOG
            System.out.println("Could not load file, IO Error");
            e.printStackTrace();
            return null;
        }
        return topic;
    }
}
