package com.mlaf.hu.brokeragent;

import com.mlaf.hu.brokeragent.exceptions.TopicNotManagedException;
import jade.util.Logger;

import java.io.*;

import static com.mlaf.hu.brokeragent.BrokerAgent.brokerAgentLogger;

public class PersistenceHelper {
    //FIXME I CAN BE DELETED, RIGHT?

    public static String getBasePath() {
        return System.getProperty("user.home") + File.separator + "MLAF" + File.separator + "BrokerAgent" + File.separator;
    }

    protected static boolean createBasePathDirs() {
        return (new File(getBasePath()).mkdirs()); // Return success
    }

    public static void storeObject(Object obj, String name) {
        //TODO configurable storagepath
        createBasePathDirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getBasePath() + name + ".ser"))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            brokerAgentLogger.log(Logger.SEVERE, () -> String.format("Could not write object %s to disk %nError %s" , obj.toString(), e.getMessage()));
        }
    }

    public static Topic loadTopic(String name) throws TopicNotManagedException {
        createBasePathDirs();
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
