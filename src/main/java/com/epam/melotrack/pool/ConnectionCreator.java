package com.epam.melotrack.pool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class ConnectionCreator {

    private final static Logger logger = LogManager.getLogger();
    public final static String DATABASE_PROPERTIES_FILE_PATH = "database";

    public final static String DATABASE_URL = "database_url";
    public final static String USERNAME = "username";
    public final static String PASSWORD = "password";

    private ConnectionCreator(){}

    public static Collection<ProxyConnection> createConnections(int connectionsAmount){
        Collection<ProxyConnection> connections = null;
        if(connectionsAmount > 0){
            connections = new ArrayList<>();
            ResourceBundle resourceBundle = ResourceBundle.getBundle(DATABASE_PROPERTIES_FILE_PATH);
            String databaseUrl = resourceBundle.getString(DATABASE_URL);
            String userName = resourceBundle.getString(USERNAME);
            String password = resourceBundle.getString(PASSWORD);
            try{
                for(int i = 0; i < connectionsAmount; i++){
                    connections.add(new ProxyConnection(DriverManager.getConnection(databaseUrl, userName, password)));
                }
            } catch (SQLException e){
                logger.fatal("Database connection error!", e);
                throw new RuntimeException(e);
            }
        } else {
            logger.error("Invalid amount of connections : " + connectionsAmount);
        }
        return connections;
    }

}
