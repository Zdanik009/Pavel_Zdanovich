package com.epam.melotrack.dao;

import com.epam.melotrack.entity.Game;
import com.epam.melotrack.exception.DaoException;

import java.util.List;

public interface GameDao extends Dao<Game> {

    List<Long> findAllToursIdByGameId(Long gameId) throws DaoException;

    List<String> findAllTitles() throws DaoException;

    @Override
    Game findById(long gameId) throws DaoException;

    Game findByTitle(String gameTitle) throws DaoException;

    Long findIdByTitle(String gameTitle) throws DaoException;

    String findTitleById(Long gameId) throws DaoException;

    String findTitleBySetOfToursId(Long setOfToursId) throws DaoException;

    List<Long> updateAllToursIdByGameId(Long gameId, List<Long> toursId) throws DaoException;

    List<Long> putAllToursIdByGameId(Long gameId, List<Long> toursId) throws DaoException;

    List<Long> deleteAllToursIdByGameId(Long gameId, List<Long> toursId) throws DaoException;

    Long findSetOfToursIdByGameIdAndTourId(Long gameId, Long tourId) throws DaoException;

}
