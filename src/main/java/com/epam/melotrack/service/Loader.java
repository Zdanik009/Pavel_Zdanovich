package com.epam.melotrack.service;

import com.epam.melotrack.dao.Dao;
import com.epam.melotrack.entity.Entity;
import com.epam.melotrack.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

class Loader<E extends Entity, P> extends Thread {

    private final static Logger logger = LogManager.getLogger();
    private Dao<E> dao;
    private P parameter;
    private E content;
    private AtomicBoolean contentLoaded;

    Loader(Dao<E> dao, P parameter) {
        this.dao = dao;
        this.parameter = parameter;
        this.contentLoaded = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        try {
            this.content = this.dao.findById((long) this.parameter);
            this.contentLoaded.set(true);
        } catch (DaoException e) {
            logger.error("Dao loading error in content loader #" + this.getId() + " due to", e);
        }
    }

    void load() {
        if (!this.contentLoaded.get()) {
            this.start();
        }
    }

    E getContent() {
        try {
            this.join();
        } catch (InterruptedException e) {
            logger.error("Loader #" + this.getId() + " was interrupted!");
        }
        return this.content;
    }

}
