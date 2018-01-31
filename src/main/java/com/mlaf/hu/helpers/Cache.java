package com.mlaf.hu.helpers;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache<T> extends ArrayList<T>{
    private static final Logger logger = Logger.getLogger(Cache.class.getName());
    
    private int maxSize;
    private boolean notifyOverflow;
    
    public Cache(){
        super();
        maxSize = 10;
        notifyOverflow = false;
    }
    
    public Cache(int size){
        super();
        maxSize = size;
        notifyOverflow = false;
    }
    
    public void notifyOverflow(boolean notify){
        notifyOverflow = notify;
    }
    
    public void setSize(int size){
        this.maxSize = size;
    }
    
    @Override
    public boolean add(T item){
        if(super.size() + 1 > maxSize){
            if(notifyOverflow)
                logger.log(Level.WARNING, "Cache overflow, messages are getting lost.");
         
            super.remove(0);
        }    
        
        return super.add(item);
    }
}
