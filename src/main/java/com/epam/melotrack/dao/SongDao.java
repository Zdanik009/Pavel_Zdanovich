package com.epam.melotrack.dao;

import com.epam.melotrack.entity.Song;
import com.epam.melotrack.exception.DaoException;

import java.util.List;

public interface SongDao extends Dao<Song> {

    List<String> findAllTitles() throws DaoException;

    @Override
    Song findById(long songId) throws DaoException;

    Song findByTitle(String songTitle) throws DaoException;

    Long findIdByTitle(String songTitle) throws DaoException;

}
