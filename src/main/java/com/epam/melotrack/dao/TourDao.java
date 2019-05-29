package com.epam.melotrack.dao;

import com.epam.melotrack.entity.Song;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.exception.DaoException;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TourDao extends Dao<Tour> {

    Map<Long, String> findAllSongsIdAndPlaybackByTourId(long tourId) throws DaoException;

    List<String> findAllTitles() throws DaoException;

    File findTemplateTourRules() throws DaoException;

    @Override
    Tour findById(long tourId) throws DaoException;

    Tour findByTitle(String tourTitle) throws DaoException;

    Long findIdByTitle(String tourTitle) throws DaoException;

    String findTitleById(Long tourId) throws DaoException;

    String findTitleBySetOfToursId(Long setOfToursId) throws DaoException;

    Map<Long, String> updateAllSongsIdAndPlaybackByTourId(long tourId, Map<Long, String> songsIdAndPlayback) throws DaoException;

    Map<Long, String> putAllSongsIdAndPlaybackByTourId(long tourId, Map<Long, String> songsIdAndPlayback) throws DaoException;

    Map<Long, String> deleteAllSongsIdAndPlaybackByTourId(long tourId, Map<Long, String> songsIdAndPlayback) throws DaoException;

}
