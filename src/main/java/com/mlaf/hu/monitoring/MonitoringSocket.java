package com.mlaf.hu.monitoring;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

class MonitoringSocket {
    private static final Logger logger = Logger.getLogger(MonitoringSocket.class.getName());
            
    private DataOutputStream outputStream;
    private Socket client;
    private final String ip;
    private final int port;
    private String delimiter;
    
    public MonitoringSocket(String ip, int port){
        if(ip.equals("localhost")){
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                logger.log(Level.WARNING, "MonitoringSocket could not resolve ip address: {0}", ex);
            }
        }
        
        this.ip = ip;
        this.port = port;
        delimiter = "";
    }
    
    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }
    
    public void send(String message) throws IOException{
        client = new Socket(ip, port);
        outputStream = new DataOutputStream(client.getOutputStream());
        outputStream.writeBytes(message + delimiter);
        outputStream.flush();
        outputStream.close();
        client.close();
    }
    
    public void close(){        
        if(client == null)
            return;
        
        try {
            client.close();
            outputStream.close();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "MonitoringSocket could not be closed.", ex);
        }
    }

}
