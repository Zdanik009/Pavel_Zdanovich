package com.epam.melotrack.dao.impl;

import com.epam.melotrack.dao.TourDao;
import com.epam.melotrack.entity.Song;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.pool.ConnectionPool;
import com.epam.melotrack.pool.ProxyConnection;
import com.epam.melotrack.service.ContentLoader;
import com.epam.melotrack.servlet.RequestContent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.melotrack.service.Service.*;

public class TourDaoImpl implements TourDao {

    private final static Logger logger = LogManager.getLogger();
    private static ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static File blankTemporaryDirectory;
    public final static String TEMPORARY_DIRECTORY_NAME = "blank";

    public final static String SQL_SELECT_ALL_TOURS = "SELECT tour_id, title, rules, time, type, categories FROM tours WHERE NOT title = 'template' AND NOT title = 'random'";
    public final static String SQL_UPDATE_ALL_TOURS = "UPDATE INTO tours SET tour_id = ?, title = ?, rules = ?, time = ?, type = ?, categories = ?";
    public final static String SQL_INSERT_ALL_TOURS = "INSERT INTO tours (tour_id, title, rules, time, type, categories) VALUES (?,?,?,?,?,?)";
    public final static String SQL_DELETE_ALL_TOURS = "DELETE FROM tours WHERE tour_id = ? AND title = ? AND rules = ? AND time = ? AND type = ? AND categories = ?";
    public final static String SQL_SELECT_ALL_TITLES = "SELECT title FROM tours WHERE NOT title = 'template' AND NOT title = 'random'";

    public final static String SQL_SELECT_SONG_ID_BY_TOUR_ID = "SELECT song_id, playback FROM sets_of_songs WHERE tour_id = ?";
    public final static String SQL_UPDATE_SONG_ID_BY_TOUR_ID = "UPDATE INTO sets_of_songs SET song_id = ?, playback = ? WHERE tour_id = ?";
    public final static String SQL_INSERT_SONG_ID_BY_TOUR_ID = "INSERT INTO sets_of_songs (tour_id, song_id, playback) VALUES (?,?,?)";
    public final static String SQL_DELETE_SONG_ID_BY_TOUR_ID = "DELETE FROM sets_of_songs WHERE tour_id = ? AND song_id = ? AND playback = ?";

    public final static String SQL_SELECT_TOUR_BY_ID = "SELECT tour_id, title, rules, time, type, categories FROM tours WHERE tour_id = ? AND NOT title = 'template'";
    public final static String SQL_SELECT_TOUR_TITLE_BY_ID = "SELECT title FROM tours WHERE tour_id = ?";
    public final static String SQL_SELECT_TOUR_BY_TITLE = "SELECT tour_id, title, rules, time, type, categories FROM tours WHERE title = ? AND NOT title = 'template'";
    public final static String SQL_SELECT_TOUR_ID_BY_TITLE = "SELECT tour_id FROM tours WHERE title = ? AND NOT title = 'template' AND NOT title = 'random'";
    public final static String SQL_SELECT_TOUR_TITLE_BY_SET_OF_TOURS_ID = "SELECT title FROM tours WHERE tour_id IN (SELECT tour_id FROM sets_of_tours WHERE set_of_tours_id = ?)";
    public final static String SQL_SELECT_TEMPLATE_RULES = "SELECT rules FROM tours WHERE title = 'template'";
    public final static String SQL_SELECT_RESERVED_TOUR_ID = "SELECT tour_id FROM tours WHERE title = ?";
    public final static String SQL_SELECT_10_SONGS_BY_RANDOM = "SELECT song_id FROM songs ORDER BY RAND() LIMIT 10";

    public TourDaoImpl() {
        connectionPool = ConnectionPool.getInstance();
        blankTemporaryDirectory = new File(RequestContent.getServletContextRealPath(), TEMPORARY_DIRECTORY_NAME);
        if (blankTemporaryDirectory.mkdir()) {
            logger.info("Temporary directory for blanks created");
        } else if (blankTemporaryDirectory.exists()) {
            logger.info("Temporary directory for blanks is already exists");
        }
    }

    private File convertBlobToFile(Blob blob, String fileName) throws DaoException {
        File temp = new File(blankTemporaryDirectory, fileName);
        try (InputStream inputStream = blob.getBinaryStream();
             OutputStream fileOutputStream = new FileOutputStream(temp)) {
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (FileNotFoundException e) {
            logger.fatal("File not found by path : " + ServletContext.TEMPDIR + File.pathSeparator + fileName, e);
            throw new DaoException("File not found by path : " + ServletContext.TEMPDIR + File.pathSeparator + fileName, e);
        } catch (IOException e) {
            logger.fatal("Reading binary stream from blob : " + blob + " failed due to ", e);
            throw new DaoException("Reading binary stream from blob : " + blob + " failed due to ", e);
        } catch (SQLException e) {
            logger.error("Creating binary stream from blob : " + blob + " failed due to ", e);
            throw new DaoException("Creating binary stream from blob : " + blob + " failed due to ", e);
        }
        return temp;
    }

    private File convertBinaryStreamToFile(InputStream inputStream, String filename) throws DaoException {
        File temp = new File(blankTemporaryDirectory, filename);
        try {
            FileUtils.copyInputStreamToFile(inputStream, temp);
        } catch (IOException e) {
            logger.fatal("Reading binary stream : " + inputStream + " failed due to ", e);
            throw new DaoException("Reading binary stream : " + inputStream + " failed due to ", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.fatal("Reading binary stream : " + inputStream + " failed due to ", e);
                throw new DaoException("Reading binary stream : " + inputStream + " failed due to ", e);
            }
        }
        return temp;
    }

    private List<Song> findSongsByTourId(long tourId) throws DaoException {
        List<Song> songs = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            Map<Long, String> songsIdAndPlayback = new HashMap<>();
            if (tourId == Tour.RANDOM_TOUR_ID) {
                preparedStatement = connection.prepareStatement(SQL_SELECT_10_SONGS_BY_RANDOM);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    songsIdAndPlayback.put(resultSet.getLong(SONG + UNDERSCORE + ID), DEFAULT_SONG_INFO);
                }
            } else {
                songsIdAndPlayback = findAllSongsIdAndPlaybackByTourId(tourId);
            }
            SongDaoImpl songDao = new SongDaoImpl();
            ContentLoader<Song, Long> contentLoader = new ContentLoader<>(songDao);
            songsIdAndPlayback.keySet().forEach(songId -> contentLoader.findById(songId));
            songs = new ArrayList<>(contentLoader.getContent());
            for (Song song : songs) {
                song.setPlayback(songsIdAndPlayback.get(song.getSongId()));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findSongsByTourId", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return songs;
    }

    @Override
    public Tour findByTitle(String tourTitle) throws DaoException {
        Tour tour = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_TOUR_BY_TITLE);
            preparedStatement.setString(1, tourTitle);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tour = new Tour();
                tour.setTourId(resultSet.getLong(TOUR + UNDERSCORE + ID));
                tour.setTitle(resultSet.getString(TITLE));
                tour.setRules(convertBinaryStreamToFile(resultSet.getBinaryStream(RULES), tour.getTitle() + DOT + JSP));
                tour.setTime(resultSet.getByte(TIME));
                tour.setType(resultSet.getString(TYPE));
                tour.setCategories(List.of(resultSet.getString(CATEGORIES).split(COMMA)));
                tour.setSongs(findSongsByTourId(tour.getTourId()));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return tour;
    }

    @Override
    public Tour findById(long tourId) throws DaoException {
        Tour tour = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_TOUR_BY_ID);
            preparedStatement.setLong(1, tourId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tour = new Tour();
                tour.setTourId(resultSet.getLong(TOUR + UNDERSCORE + ID));
                tour.setTitle(resultSet.getString(TITLE));
                tour.setRules(convertBinaryStreamToFile(resultSet.getBinaryStream(RULES), tour.getTitle() + DOT + JSP));
                tour.setTime(resultSet.getByte(TIME));
                tour.setType(resultSet.getString(TYPE));
                tour.setCategories(List.of(resultSet.getString(CATEGORIES).split(COMMA)));
                tour.setSongs(findSongsByTourId(tour.getTourId()));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return tour;
    }

    @Override
    public String findTitleById(Long tourId) throws DaoException {
        String tourTitle = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        if (tourId != Tour.TEMPLATE_TOUR_ID && tourId != Tour.RANDOM_TOUR_ID) {
            try {
                connection = connectionPool.getConnection();
                preparedStatement = connection.prepareStatement(SQL_SELECT_TOUR_TITLE_BY_ID);
                preparedStatement.setLong(1, tourId);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    tourTitle = resultSet.getString(TITLE);
                }
            } catch (SQLException e) {
                logger.error("Database statement creating error!", e);
                throw new DaoException("Database statement creating error in method findById", e);
            } finally {
                close(resultSet, preparedStatement, connection);
            }
        }
        return tourTitle;
    }

    @Override
    public Long findIdByTitle(String tourTitle) throws DaoException {
        Long tourId = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_TOUR_ID_BY_TITLE);
            preparedStatement.setString(1, tourTitle);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tourId = resultSet.getLong(TOUR + UNDERSCORE + ID);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return tourId;
    }

    @Override
    public String findTitleBySetOfToursId(Long setOfToursId) throws DaoException {
        String gameTitle = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_TOUR_TITLE_BY_SET_OF_TOURS_ID);
            preparedStatement.setLong(1, setOfToursId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                gameTitle = resultSet.getString(TITLE);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findIdBySetOfToursId", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return gameTitle;
    }

    @Override
    public List<String> findAllTitles() throws DaoException {
        List<String> tourTitles = new ArrayList<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_TITLES);
            while (resultSet.next()) {
                tourTitles.add(resultSet.getString(TITLE));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return tourTitles;
    }

    @Override
    public File findTemplateTourRules() throws DaoException {
        File templateTourRules = null;
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_TEMPLATE_RULES);
            if (resultSet.next()) {
                templateTourRules = convertBinaryStreamToFile(resultSet.getBinaryStream(RULES), Tour.TITLE_TEMPLATE + DOT + JSP);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return templateTourRules;
    }

    @Override
    public List<Tour> findAll() throws DaoException {
        List<Tour> tours = new ArrayList<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_TOURS);
            while (resultSet.next()) {
                Tour tour = new Tour();
                tour.setTourId(resultSet.getLong(TOUR + UNDERSCORE + ID));
                tour.setTitle(resultSet.getString(TITLE));
                tour.setRules(convertBinaryStreamToFile(resultSet.getBinaryStream(RULES), tour.getTitle() + DOT + JSP));
                tour.setTime(resultSet.getByte(TIME));
                tour.setSongs(findSongsByTourId(tour.getTourId()));
                tour.setType(resultSet.getString(TYPE));
                tour.setCategories(List.of(resultSet.getString(CATEGORIES).split(COMMA)));
                tours.add(tour);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return tours;
    }

    @Override
    public List<Tour> updateAll(List<Tour> entities) throws DaoException {
        List<Tour> updatedTours = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        InputStream inputStream = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_UPDATE_ALL_TOURS);
            for (Tour tour : entities) {
                if (!tour.getTitle().equals(RANDOM) && !tour.getTitle().equals(TEMPLATE)) {
                    preparedStatement.setLong(1, tour.getTourId());
                    preparedStatement.setString(2, tour.getTitle());
                    inputStream = new FileInputStream(tour.getRules());
                    preparedStatement.setBinaryStream(3, inputStream);
                    preparedStatement.setByte(4, tour.getTime());
                    preparedStatement.setString(5, tour.getType());
                    preparedStatement.setString(6, tour.getCategories().stream().collect(Collectors.joining(COMMA)));
                    if (preparedStatement.executeUpdate() != 0) {
                        updatedTours.add(tour);
                    }
                    preparedStatement.clearParameters();
                }
            }
        } catch (FileNotFoundException e) {
            logger.fatal("Loading file error due to ", e);
            throw new DaoException("Loading file error due to ", e);
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method updateAll", e);
        } finally {
            close(null, preparedStatement, connection);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.fatal("Closing input stream error due to ", e);
            }
        }
        return updatedTours;
    }

    @Override
    public List<Tour> putAll(List<Tour> entities) throws DaoException {
        List<Tour> puttedTours = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        InputStream inputStream = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_INSERT_ALL_TOURS);
            for (Tour tour : entities) {
                if (!tour.getTitle().equals(RANDOM) && !tour.getTitle().equals(TEMPLATE)) {
                    preparedStatement.setLong(1, tour.getTourId());
                    preparedStatement.setString(2, tour.getTitle());
                    inputStream = new FileInputStream(tour.getRules());
                    preparedStatement.setBinaryStream(3, inputStream);
                    preparedStatement.setByte(4, tour.getTime());
                    preparedStatement.setString(5, tour.getType());
                    preparedStatement.setString(6, tour.getCategories().stream().collect(Collectors.joining(COMMA)));
                    if (preparedStatement.executeUpdate() != 0) {
                        puttedTours.add(tour);
                    }
                    preparedStatement.clearParameters();
                }
            }
        } catch (FileNotFoundException e) {
            logger.fatal("Loading file error due to ", e);
            throw new DaoException("Loading file error due to ", e);
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method putAll", e);
        } finally {
            close(null, preparedStatement, connection);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.fatal("Closing input stream error due to ", e);
                throw new DaoException("Closing input stream error due to ", e);
            }
        }
        return puttedTours;
    }

    @Override
    public List<Tour> deleteAll(List<Tour> entities) throws DaoException {
        List<Tour> deletedTours = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        InputStream inputStream = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_DELETE_ALL_TOURS);
            for (Tour tour : entities) {
                if (!tour.getTitle().equals(RANDOM) && !tour.getTitle().equals(TEMPLATE)) {
                    preparedStatement.setLong(1, tour.getTourId());
                    preparedStatement.setString(2, tour.getTitle());
                    inputStream = new FileInputStream(tour.getRules());
                    preparedStatement.setBinaryStream(3, inputStream);
                    preparedStatement.setByte(4, tour.getTime());
                    preparedStatement.setString(5, tour.getType());
                    preparedStatement.setString(6, tour.getCategories().stream().collect(Collectors.joining(COMMA)));
                    if (preparedStatement.executeUpdate() != 0) {
                        deletedTours.add(tour);
                    }
                    preparedStatement.clearParameters();
                }
            }
        } catch (FileNotFoundException e) {
            logger.fatal("Loading file error due to ", e);
            throw new DaoException("Loading file error due to ", e);
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method deleteAll", e);
        } finally {
            close(null, preparedStatement, connection);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.fatal("Closing input stream error due to ", e);
                throw new DaoException("Closing input stream error due to ", e);
            }
        }
        return deletedTours;
    }

    @Override
    public Map<Long, String> findAllSongsIdAndPlaybackByTourId(long tourId) throws DaoException {
        Map<Long, String> songs = new HashMap<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        if (isValidTourId(tourId)) {
            try {
                connection = connectionPool.getConnection();
                preparedStatement = connection.prepareStatement(SQL_SELECT_SONG_ID_BY_TOUR_ID);
                preparedStatement.setLong(1, tourId);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    songs.put(resultSet.getLong(SONG + UNDERSCORE + ID), resultSet.getString(PLAYBACK));
                }
            } catch (SQLException e) {
                logger.error("Database statement creating error!", e);
                throw new DaoException("Database statement creating error in method findSongsByTourId", e);
            } finally {
                close(resultSet, preparedStatement, connection);
            }
        }
        return songs;
    }

    @Override
    public Map<Long, String> updateAllSongsIdAndPlaybackByTourId(long tourId, Map<Long, String> songsIdAndPlayback) throws DaoException {
        Map<Long, String> updatedSongs = new HashMap<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        if (isValidTourId(tourId)) {
            try {
                connection = connectionPool.getConnection();
                preparedStatement = connection.prepareStatement(SQL_UPDATE_SONG_ID_BY_TOUR_ID);
                preparedStatement.setLong(3, tourId);
                for (Map.Entry<Long, String> song : songsIdAndPlayback.entrySet()) {
                    preparedStatement.setLong(1, song.getKey());
                    preparedStatement.setString(2, song.getValue());
                    if (preparedStatement.executeUpdate() != 0) {
                        updatedSongs.put(song.getKey(), song.getValue());
                    }
                }
            } catch (SQLException e) {
                logger.error("Database statement creating error!", e);
                throw new DaoException("Database statement creating error in method findSongsByTourId", e);
            } finally {
                close(resultSet, preparedStatement, connection);
            }
        }
        return updatedSongs;
    }

    @Override
    public Map<Long, String> putAllSongsIdAndPlaybackByTourId(long tourId, Map<Long, String> songsIdAndPlayback) throws DaoException {
        Map<Long, String> puttedSongsId = new HashMap<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        if (isValidTourId(tourId)) {
            try {
                connection = connectionPool.getConnection();
                preparedStatement = connection.prepareStatement(SQL_INSERT_SONG_ID_BY_TOUR_ID);
                preparedStatement.setLong(1, tourId);
                for (Map.Entry<Long, String> song : songsIdAndPlayback.entrySet()) {
                    preparedStatement.setLong(2, song.getKey());
                    preparedStatement.setString(3, song.getValue());
                    if (preparedStatement.executeUpdate() != 0) {
                        puttedSongsId.put(song.getKey(), song.getValue());
                    }
                }
            } catch (SQLException e) {
                logger.error("Database statement creating error!", e);
                throw new DaoException("Database statement creating error in method findSongsByTourId", e);
            } finally {
                close(resultSet, preparedStatement, connection);
            }
        }
        return puttedSongsId;
    }

    @Override
    public Map<Long, String> deleteAllSongsIdAndPlaybackByTourId(long tourId, Map<Long, String> songsIdAndPlayback) throws DaoException {
        Map<Long, String> deletedSongs = new HashMap<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        if (isValidTourId(tourId)) {
            try {
                connection = connectionPool.getConnection();
                preparedStatement = connection.prepareStatement(SQL_DELETE_SONG_ID_BY_TOUR_ID);
                preparedStatement.setLong(1, tourId);
                for (Map.Entry<Long, String> song : songsIdAndPlayback.entrySet()) {
                    preparedStatement.setLong(2, song.getKey());
                    preparedStatement.setString(3, song.getValue());
                    if (preparedStatement.executeUpdate() != 0) {
                        deletedSongs.put(song.getKey(), song.getValue());
                    }
                }
            } catch (SQLException e) {
                logger.error("Database statement creating error!", e);
                throw new DaoException("Database statement creating error in method findSongsByTourId", e);
            } finally {
                close(resultSet, preparedStatement, connection);
            }
        }
        return deletedSongs;
    }

    public boolean isValidTourId(Long tourId) throws DaoException {
        boolean result = false;
        List<String> reservedTourTitles = new ArrayList<>();
        reservedTourTitles.add(RANDOM);
        reservedTourTitles.add(TEMPLATE);
        List<Long> reservedToursId = new ArrayList<>(reservedTourTitles.size());
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_RESERVED_TOUR_ID);
            for (String tourTitle : reservedTourTitles) {
                preparedStatement.setString(1, tourTitle);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    reservedToursId.add(resultSet.getLong(TOUR + UNDERSCORE + ID));
                }
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method isValidTourId", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return !reservedToursId.contains(tourId);
    }

    public static Path getBlankTemporaryDirectory() {
        return blankTemporaryDirectory.toPath();
    }

}
