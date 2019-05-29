package com.epam.melotrack.service;

import com.epam.melotrack.dao.Dao;
import com.epam.melotrack.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContentLoader<E extends Entity, P> {

    private List<Loader<E, P>> loaders;
    private Dao<E> dao;

    public ContentLoader(Dao<E> dao) {
        loaders = new ArrayList<>();
        this.dao = dao;
    }

    public void findById(P id) {
        Loader<E, P> loader = new Loader<>(this.dao, id);
        loader.load();
        loaders.add(loader);
    }

    public Collection<E> getContent() {
        Collection<E> loadedContent = new ArrayList<>();
        loaders.forEach(loader -> loadedContent.add(loader.getContent()));
        return loadedContent;
    }

}
