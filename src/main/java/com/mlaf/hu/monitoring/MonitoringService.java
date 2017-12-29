package com.mlaf.hu.monitoring;

import com.mlaf.hu.helpers.Cache;
import com.mlaf.hu.helpers.Configuration;
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.Profile;
import jade.core.ServiceException;
import jade.core.VerticalCommand;
import jade.core.messaging.GenericMessage;
import jade.core.messaging.MessagingSlice;
import jade.lang.acl.ACLMessage;
import java.util.Iterator;
import java.util.logging.Logger;

public abstract class MonitoringService extends BaseService{
    protected static final Logger logger = Logger.getLogger(MonitoringService.class.getName());
    public static final String NAME = "mlaf-java.monitoringservice";
    
    private final Filter outFilter = new OutgoingLoggingFilter();
    
    private Cache<ACLMessage> cache;
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void boot(Profile p) throws ServiceException{
        super.boot(p);
        
        Configuration config = Configuration.getInstance();
        
        String cacheSizeStr = config.getProperty("monitoring.cache_size");
        int cacheSize = cacheSizeStr != null ? Integer.valueOf(cacheSizeStr) : 100;
        cacheSize = cacheSize < 1 ? 1 : cacheSize;
        
        cache = new Cache<>(cacheSize);
        cache.notifyOverflow(true);
        
        boot(p, config);
    }
    
    public abstract void boot(Profile p, Configuration config);
    
    @Override
    public void shutdown(){
        if(!cache.isEmpty()){
            dumpCache(cache);
            cache.clear();
        }
        
        onShutdown();
        super.shutdown();
    }
    
    public abstract void onShutdown();
    
    @Override
    public Filter getCommandFilter(boolean direction){
        if(direction == Filter.OUTGOING){
            return outFilter;
        }else{
            return null;
        }
    }
    
    protected void sendCache(){
        if(cache.isEmpty())
            return;
        
        Iterator<ACLMessage> iterator = cache.iterator();
        while(iterator.hasNext()){
            ACLMessage message = iterator.next();
            if(log(message))
                iterator.remove();
            else
                break;
        }
    }
    
    protected abstract void dumpCache(Cache<ACLMessage> cache);

    protected abstract boolean log(ACLMessage message);
    
    protected boolean filter(ACLMessage message){
        return true;
    }
    
    private class OutgoingLoggingFilter extends Filter{
        @Override
        public boolean accept (VerticalCommand cmd){
            if(cmd.getName().equals(MessagingSlice.SEND_MESSAGE)){
                Object[] params = cmd.getParams();
                
                GenericMessage gMsg = (GenericMessage) params[1];
                ACLMessage msg = gMsg.getACLMessage();
                
                if(msg == null)
                    return true;
                
                if(filter(msg)){
                    cache.add(msg);
                    sendCache();
                }    
            }
            return true;
        }
    }
}
