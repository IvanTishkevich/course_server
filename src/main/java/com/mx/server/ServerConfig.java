package com.mx.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public interface ServerConfig {
    public default int readConfig(){
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config/config.ini"));
            return Integer.valueOf(props.getProperty("SERVER_PORT"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
