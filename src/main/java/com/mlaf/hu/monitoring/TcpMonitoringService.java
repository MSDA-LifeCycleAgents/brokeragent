package com.mlaf.hu.monitoring;

import com.mlaf.hu.helpers.Cache;
import com.mlaf.hu.helpers.Configuration;
import jade.core.Profile;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.logging.Level;

public class TcpMonitoringService extends MonitoringService{
    private MonitoringSocket socket;
    
    @Override
    public void boot(Profile p, Configuration config) {
        String ip = config.getProperty("monitoring.server.ip");
        int port = Integer.valueOf(config.getProperty("monitoring.server.port"));
        
        socket = new MonitoringSocket(ip, port);
        
        String delimiter = config.getProperty("monitoring.server.message_delimiter");
        if(delimiter != null)
            socket.setDelimiter(delimiter);
        
    }
    
    @Override
    protected boolean log(ACLMessage message) {
        try {
            socket.send(message.toString());
            return true;
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Log message could not be sent. {0}", ex.getMessage());
            return false;
        }
    }

    @Override
    public void onShutdown() {
        logger.log(Level.INFO, "MonitoringService shutting down.");
        socket.close();
    }
    
    @Override
    protected void dumpCache(Cache<ACLMessage> cache){
        String dump = "";
        dump = cache.stream().map((message) -> message.toString() + "\n").reduce(dump, String::concat);
        logger.log(Level.INFO, "Dumping cached messages:\n {0}", dump);
    }
    
//    @Override
//    protected boolean filter(ACLMessage message){
//        if(message == null || message.getContent() == null)
//            return false;
//        
//        return message.getContent().startsWith("Foreign");
//    }
}
