/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlaf.hu.helpers;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Rogier
 */
public class Configuration {
    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    
    private static Configuration configuration;
    private final Properties properties;
    private static final String DEV_RESOURCE_PROP_FILE_NAME = "src/main/resources/config.properties";
    private static final String USER_HOME_PROP_FILE_NAME = "/.config/MLAF/config.properties";
    
    private Configuration(){
        properties = new Properties();
        loadProperties();
    }
    
    public static Configuration getInstance(){
        if(configuration == null){
            configuration = new Configuration();
        }
        return configuration;
    }
    
    private void loadProperties(){
        try{
            InputStream input = getConfigStream();
            properties.load(input);
        }catch(IOException e){
            logger.log(Level.WARNING, "Failed to load configuration properties: {0}", e.getMessage());
        }
    }

    private InputStream getConfigStream() throws IOException {
        // Config file for during development P0
        if (fileExists(DEV_RESOURCE_PROP_FILE_NAME)) {
            logger.log(Level.INFO, "Using config in current path: " + DEV_RESOURCE_PROP_FILE_NAME);
            return new FileInputStream(DEV_RESOURCE_PROP_FILE_NAME);
        }

        // Config file in user home P1
        String userHome = System.getProperty("user.home");
        String userHomeConfPath = userHome + USER_HOME_PROP_FILE_NAME;
        if (fileExists(userHomeConfPath)) {
            logger.log(Level.INFO, "Using config in user home: " + userHomeConfPath);
            return new FileInputStream(userHomeConfPath);
        }

        // Does not exist, try and create a config
        try {
            logger.log(Level.INFO, "Creating default config in " + userHomeConfPath);
            createDefaultFile(userHomeConfPath);
            return readDefaultPackagedConfig();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not create default config!", e);
        }
        logger.log(Level.INFO, "Using default packaged config");
        return readDefaultPackagedConfig();
    }

    private InputStream readDefaultPackagedConfig() {
        return getClass().getResourceAsStream("/config.properties");
    }


    private boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }

    private void createDefaultFile(String pathString) throws IOException {
        Path file = Paths.get(pathString);
        Files.createDirectories(file.getParent());
        Files.copy(readDefaultPackagedConfig(), file);
    }
    
    public String getProperty(String property){
        return properties.getProperty(property);
    }
}
