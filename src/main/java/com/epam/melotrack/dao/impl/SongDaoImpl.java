package com.epam.melotrack.dao.impl;

import com.epam.melotrack.converter.AudioFormat;
import com.epam.melotrack.converter.AudioFormatConverter;
import com.epam.melotrack.dao.SongDao;
import com.epam.melotrack.entity.Song;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.pool.ConnectionPool;
import com.epam.melotrack.pool.ProxyConnection;
import com.epam.melotrack.servlet.RequestContent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.*;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

import static com.epam.melotrack.service.Service.*;

public class SongDaoImpl implements SongDao {

    private final static Logger logger = LogManager.getLogger();
    private static ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static File audioTemporaryDirectory;
    private final String TEMPORARY_DIRECTORY_NAME = "audio";
    public final static AudioFormat DEFAULT_AUDIO_FORMAT = AudioFormat.MP3;

    public final static String SQL_SELECT_ALL_SONGS = "SELECT song_id, musician, title, file, genre, album, date, info FROM songs";
    public final static String SQL_SELECT_ALL_SONG_TITLES = "SELECT musician, title FROM songs";
    public final static String SQL_SELECT_ALL_MUSICIANS = "SELECT musician FROM songs";
    public final static String SQL_UPDATE_ALL_SONGS = "UPDATE INTO songs SET song_id = ?, musician = ?, title = ?, file = ?, genre = ?, album = ?, date = ?, info = ?";
    public final static String SQL_INSERT_ALL_SONGS = "INSERT INTO songs (song_id, musician, title, file, genre, album, date, info) VALUES (?,?,?,?,?,?,?,?)";
    public final static String SQL_DELETE_ALL_SONGS = "DELETE FROM songs WHERE song_id = ? AND musician = ? AND title = ? AND file = ? AND genre = ? AND album = ? AND date = ? AND info = ?";

    public final static String SQL_SELECT_SONG_BY_ID = "SELECT song_id, musician, title, file, genre, album, date, info FROM songs WHERE song_id = ?";
    public final static String SQL_SELECT_SONG_ID_BY_TITLE = "SELECT song_id FROM songs WHERE musician = ? AND title = ?";
    public final static String SQL_SELECT_SONG_BY_TITLE = "SELECT song_id, musician, title, file, genre, album, date, info FROM songs WHERE title = ?";

    public SongDaoImpl() {
        connectionPool = ConnectionPool.getInstance();
        audioTemporaryDirectory = new File(RequestContent.getServletContextRealPath(), TEMPORARY_DIRECTORY_NAME);
        audioTemporaryDirectory.deleteOnExit();
        if (audioTemporaryDirectory.mkdir()) {
            logger.info("Temporary directory for audio created");
        } else if (audioTemporaryDirectory.exists()) {
            logger.info("Temporary directory for audio is already exists");
        }
    }

    private File convertBlobToFile(Blob blob, String fileName, AudioFormat audioFormat) throws DaoException {
        File temp = new File(audioTemporaryDirectory, fileName + audioFormat.toString());
        try (InputStream inputStream = blob.getBinaryStream();
             OutputStream fileOutputStream = new FileOutputStream(temp)) {
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (FileNotFoundException e) {
            logger.fatal("File not found by path : " + TEMPORARY_DIRECTORY_NAME + File.pathSeparator + fileName, e);
            throw new DaoException("File not found by path : " + TEMPORARY_DIRECTORY_NAME + File.pathSeparator + fileName, e);
        } catch (IOException e) {
            logger.fatal("Reading binary stream from blob : " + blob + " failed due to ", e);
            throw new DaoException("Reading binary stream from blob : " + blob + " failed due to ", e);
        } catch (SQLException e) {
            logger.error("Creating binary stream from blob : " + blob + " failed due to ", e);
            throw new DaoException("Creating binary stream from blob : " + blob + " failed due to ", e);
        }
        return temp;
    }

    private File convertBinaryStreamToFile(InputStream inputStream, String fileName, AudioFormat audioFormat) throws DaoException {
        File temp = new File(audioTemporaryDirectory, fileName + audioFormat.toString());
        try {
            if (audioFormat.equals(DEFAULT_AUDIO_FORMAT)) {
                FileUtils.copyInputStreamToFile(inputStream, temp);
            } else {
                AudioFormatConverter audioFormatConverter = new AudioFormatConverter();
                audioFormatConverter.convert(inputStream, audioFormat, temp);
            }
        } catch (IOException e) {
            logger.fatal("Reading binary stream : " + inputStream + " failed due to ", e);
            throw new DaoException("Reading binary stream : " + inputStream + " failed due to ", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.fatal("Reading binary stream : " + inputStream + " failed due to ", e);
            }
        }
        return temp;
    }

    @Override
    public List<String> findAllTitles() throws DaoException {
        List<String> songTitles = new ArrayList<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_SONG_TITLES);
            while (resultSet.next()) {
                String musician = resultSet.getString(MUSICIAN);
                String title = resultSet.getString(TITLE);
                String result = musician.concat(DASH).concat(title);
                songTitles.add(result);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return songTitles;
    }

    @Override
    public Song findById(long songId) throws DaoException {
        Song song = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_SONG_BY_ID);
            preparedStatement.setLong(1, songId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                song = new Song();
                song.setSongId(resultSet.getLong(SONG + UNDERSCORE + ID));
                song.setMusician(resultSet.getString(MUSICIAN));
                song.setTitle(resultSet.getString(TITLE));
                song.setFile(convertBinaryStreamToFile(resultSet.getBinaryStream(FILE), String.valueOf(songId), Song.getAudioFormat()));
                song.setGenre(resultSet.getString(GENRE));
                song.setAlbum(resultSet.getString(ALBUM));
                song.setDate(resultSet.getShort(DATE));
                song.setInformation(resultSet.getString(INFO));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return song;
    }

    @Override
    public Long findIdByTitle(String songTitle) throws DaoException {
        Long songId = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_SONG_ID_BY_TITLE);
            preparedStatement.setString(1, songTitle.substring(0, songTitle.indexOf(DASH)).trim());
            preparedStatement.setString(2, songTitle.substring(songTitle.indexOf(DASH) + 1).trim());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                songId = resultSet.getLong(SONG + UNDERSCORE + ID);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return songId;
    }

    @Override
    public Song findByTitle(String songTitle) throws DaoException {
        Song song = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_SONG_BY_TITLE);
            preparedStatement.setString(1, songTitle);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                song = new Song();
                song.setSongId(resultSet.getLong(SONG + UNDERSCORE + ID));
                song.setMusician(resultSet.getString(MUSICIAN));
                song.setTitle(resultSet.getString(TITLE));
                song.setFile(convertBinaryStreamToFile(resultSet.getBinaryStream(FILE), String.valueOf(song.getSongId()), Song.getAudioFormat()));
                song.setGenre(resultSet.getString(GENRE));
                song.setAlbum(resultSet.getString(ALBUM));
                song.setDate(resultSet.getShort(DATE));
                song.setInformation(resultSet.getNString(INFO));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return song;
    }

    @Override
    public List<Song> findAll() throws DaoException {
        List<Song> songs = new ArrayList<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_SONGS);
            while (resultSet.next()) {
                Song song = new Song();
                song.setSongId(resultSet.getLong(SONG + UNDERSCORE + ID));
                song.setMusician(resultSet.getString(MUSICIAN));
                song.setTitle(resultSet.getString(TITLE));
                song.setFile(convertBinaryStreamToFile(resultSet.getBinaryStream(FILE), String.valueOf(song.getSongId()), Song.getAudioFormat()));
                song.setGenre(resultSet.getString(GENRE));
                song.setAlbum(resultSet.getString(ALBUM));
                song.setDate(resultSet.getShort(DATE));
                song.setInformation(resultSet.getNString(INFO));
                songs.add(song);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return songs;
    }

    @Override
    public List<Song> updateAll(List<Song> entities) throws DaoException {
        List<Song> updatedSongs = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        InputStream inputStream = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_UPDATE_ALL_SONGS);
            for (Song song : entities) {
                preparedStatement.setLong(1, song.getSongId());
                preparedStatement.setString(2, song.getMusician());
                preparedStatement.setString(3, song.getTitle());
                inputStream = new FileInputStream(song.getFile());
                preparedStatement.setBinaryStream(4, inputStream);
                preparedStatement.setString(5, song.getGenre());
                preparedStatement.setString(6, song.getAlbum());
                preparedStatement.setShort(7, song.getDate());
                preparedStatement.setNString(8, song.getInformation());
                if (preparedStatement.executeUpdate() != 0) {
                    updatedSongs.add(song);
                }
                preparedStatement.clearParameters();
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
                logger.error("Closing input stream error due to ", e);
            }
        }
        return updatedSongs;
    }

    @Override
    public List<Song> putAll(List<Song> entities) throws DaoException {
        List<Song> putedSongs = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        InputStream inputStream = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_INSERT_ALL_SONGS);
            for (Song song : entities) {
                preparedStatement.setLong(1, song.getSongId());
                preparedStatement.setString(2, song.getMusician());
                preparedStatement.setString(3, song.getTitle());
                inputStream = new FileInputStream(song.getFile());
                preparedStatement.setBinaryStream(4, inputStream);
                preparedStatement.setString(5, song.getGenre());
                preparedStatement.setString(6, song.getAlbum());
                preparedStatement.setShort(7, song.getDate());
                preparedStatement.setNString(8, song.getInformation());
                if (preparedStatement.executeUpdate() != 0) {
                    putedSongs.add(song);
                }
                preparedStatement.clearParameters();
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
                logger.error("Closing input stream error due to ", e);
            }
        }
        return putedSongs;
    }

    @Override
    public List<Song> deleteAll(List<Song> entities) throws DaoException {
        List<Song> deletedSongs = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        InputStream inputStream = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_DELETE_ALL_SONGS);
            for (Song song : entities) {
                preparedStatement.setLong(1, song.getSongId());
                preparedStatement.setString(2, song.getMusician());
                preparedStatement.setString(3, song.getTitle());
                inputStream = new FileInputStream(song.getFile());
                preparedStatement.setBinaryStream(4, inputStream);
                preparedStatement.setString(5, song.getGenre());
                preparedStatement.setString(6, song.getAlbum());
                preparedStatement.setShort(7, song.getDate());
                preparedStatement.setNString(8, song.getInformation());
                if (preparedStatement.executeUpdate() != 0) {
                    deletedSongs.add(song);
                }
                preparedStatement.clearParameters();

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
                logger.error("Closing input stream error due to ", e);
            }
        }
        return deletedSongs;
    }

    public static Path getAudioTemporaryDirectoryPath() {
        return audioTemporaryDirectory.toPath();
    }
}
