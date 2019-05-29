package com.epam.melotrack.pool;

import com.mysql.jdbc.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {

    private final static Logger logger = LogManager.getLogger();
    private static ConnectionPool instance;
    public final static int DEFAULT_POOL_SIZE = 10;
    private static LinkedBlockingQueue<ProxyConnection> availableConnections = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<ProxyConnection> usedConnections = new LinkedBlockingQueue<>();
    private final static ReentrantLock LOCK = new ReentrantLock();
    private static AtomicBoolean isPoolCreated = new AtomicBoolean(false);
    private static Timer timer;

   static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            logger.fatal("Database driver registration error!", e);
            throw new RuntimeException(e);
        }
    }

    private ConnectionPool() {
        availableConnections.addAll(ConnectionCreator.createConnections(DEFAULT_POOL_SIZE));
        startPoolCheck();
    }

    public static ConnectionPool getInstance() {
        if (!isPoolCreated.get()) {
            try {
                LOCK.lock();
                if (instance == null) {
                    instance = new ConnectionPool();
                    isPoolCreated.set(true);
                }
            } finally {
                LOCK.unlock();
            }
            return instance;
        } else {
            return instance;
        }
    }

    public ProxyConnection getConnection() {
       ProxyConnection connection = null;
       try {
           connection = availableConnections.take();
           usedConnections.put(connection);
       } catch (InterruptedException e){
           logger.error("Getting connection interrupted pool thread!", e);
       }
        return connection;
    }

    public boolean putConnection(ProxyConnection connection) {
        try {
            usedConnections.remove(connection);
            if (!connection.isClosed()) {
                availableConnections.put(connection);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error("Database connection condition check error!", e);
            return false;
        } catch (InterruptedException e){
            logger.error("Getting connection interrupted pool thread!", e);
            return false;
        }
    }

    public void closeConnectionPool() {
        try {
            cancelPoolCheck();
            for (int i = 0; i < availableConnections.size(); i++) {
                availableConnections.take().closeConnection();
            }
            availableConnections.clear();
            usedConnections.clear();
            deregisterDrivers();
        } catch (SQLException e) {
            logger.error("Database connection closing error!", e);
        } catch (InterruptedException e){
            logger.fatal("Getting connection interrupted pool thread!", e);
            throw new RuntimeException(e);
        } finally {
            try {
                for (int i = 0; i < availableConnections.size(); i++) {
                    availableConnections.take().closeConnection();
                }
            } catch (SQLException e) {
                logger.error("Database connection closing error!", e);
            } catch (InterruptedException e){
                logger.fatal("Getting connection interrupted pool thread!", e);
            }
        }
    }

    private void deregisterDrivers() {
        Enumeration<java.sql.Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            try {
                java.sql.Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.error("Database drivers deregistration error!", e);
            }
        }

    }

    private static class PoolChecker extends TimerTask {

        public final static long CHECK_POOL_TIME = 1000_000_000;

        @Override
        public void run() {
            int connectionAmount = availableConnections.size() + usedConnections.size();
            if (connectionAmount < DEFAULT_POOL_SIZE) {
                availableConnections.addAll(ConnectionCreator.createConnections(DEFAULT_POOL_SIZE - connectionAmount));
            }
        }

    }

    private void startPoolCheck() {
        timer = new Timer();
        timer.schedule(new PoolChecker(), PoolChecker.CHECK_POOL_TIME);
    }

    private void cancelPoolCheck() {
        timer.cancel();
        timer = null;
    }

}
