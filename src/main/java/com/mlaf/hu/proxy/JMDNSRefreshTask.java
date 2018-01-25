package com.mlaf.hu.proxy;

import java.util.TimerTask;
import javax.jmdns.JmDNS;
import javax.jmdns.impl.JmDNSImpl;

/**
 *
 * @author Rogier
 */
public class JMDNSRefreshTask extends TimerTask{
    private final JmDNS jmdns;
    private final String mdnsType;
    
    public JMDNSRefreshTask(JmDNS jmdns, String mdnsType){
        this.jmdns = jmdns;
        this.mdnsType = mdnsType;
    }
    
    @Override
    public void run(){
        ((JmDNSImpl) jmdns).startServiceResolver(mdnsType);
    }
}
