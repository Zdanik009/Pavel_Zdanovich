package com.epam.melotrack.dao.impl;

import com.epam.melotrack.dao.GameDao;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.pool.ConnectionPool;
import com.epam.melotrack.pool.ProxyConnection;
import com.epam.melotrack.service.ContentLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.epam.melotrack.service.Service.*;

public class GameDaoImpl implements GameDao {

    private final static Logger logger = LogManager.getLogger();
    private static ConnectionPool connectionPool;

    public final static String SQL_SELECT_ALL_GAMES = "SELECT game_id, title FROM games";
    public final static String SQL_UPDATE_ALL_GAMES = "UPDATE INTO games SET game_id = ?, title = ?";
    public final static String SQL_INSERT_ALL_GAMES = "INSERT INTO games (game_id, title) VALUES (?,?)";
    public final static String SQL_DELETE_ALL_GAMES = "DELETE FROM games WHERE game_id = ?, title = ?";

    public final static String SQL_SELECT_TOUR_ID_BY_GAME_ID = "SELECT tour_id FROM sets_of_tours WHERE game_id = ?";
    public final static String SQL_UPDATE_TOUR_ID_BY_GAME_ID = "UPDATE INTO sets_of_tours SET tour_id = ? WHERE game_id = ?";
    public final static String SQL_INSERT_TOUR_ID_BY_GAME_ID = "INSERT INTO sets_of_tours (tour_id, game_id) VALUES (?,?)";
    public final static String SQL_DELETE_TOUR_ID_BY_GAME_ID = "DELETE FROM sets_of_tours WHERE tour_id = ? AND game_id = ?";

    public final static String SQL_SELECT_GAME_BY_ID = "SELECT game_id, title FROM games WHERE game_id = ?";
    public final static String SQL_SELECT_GAME_BY_TITLE = "SELECT game_id, title FROM games WHERE title = ?";
    public final static String SQL_SELECT_GAME_TITLE_BY_SET_OF_TOURS_ID = "SELECT title FROM games WHERE game_id IN (SELECT game_id FROM sets_of_tours WHERE set_of_tours_id = ?)";
    public final static String SQL_SELECT_SET_OF_TOURS_ID_BY_GAME_ID_AND_TOUR_ID = "SELECT set_of_tours_id FROM sets_of_tours WHERE game_id = ? AND tour_id = ?";

    public GameDaoImpl() {
        connectionPool = ConnectionPool.getInstance();
    }

    private List<Tour> findToursByGameId(long gameId) throws DaoException {
        List<Long> toursId = findAllToursIdByGameId(gameId);
        TourDaoImpl tourDao = new TourDaoImpl();
        ContentLoader<Tour, Long> contentLoader = new ContentLoader<>(tourDao);
        toursId.forEach(tourId -> contentLoader.findById(tourId));
        List<Tour> tours = new ArrayList<>(contentLoader.getContent());
        return tours;
    }

    @Override
    public Game findByTitle(String gameTitle) throws DaoException {
        Game game = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_GAME_BY_TITLE);
            preparedStatement.setString(1, gameTitle);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                game = new Game();
                game.setGameId(resultSet.getLong(GAME + UNDERSCORE + ID));
                game.setTitle(resultSet.getString(TITLE));
                game.setTours(findToursByGameId(game.getGameId()));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return game;
    }

    @Override
    public Game findById(long gameId) throws DaoException {
        Game game = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_GAME_BY_ID);
            preparedStatement.setLong(1, gameId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                game = new Game();
                game.setGameId(resultSet.getLong(GAME + UNDERSCORE + ID));
                game.setTitle(resultSet.getString(TITLE));
                game.setTours(findToursByGameId(game.getGameId()));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return game;
    }

    @Override
    public Long findIdByTitle(String gameTitle) throws DaoException {
        Long gameId = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_GAME_BY_TITLE);
            preparedStatement.setString(1, gameTitle);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                gameId = resultSet.getLong(GAME + UNDERSCORE + ID);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return gameId;
    }

    @Override
    public String findTitleById(Long gameId) throws DaoException {
        String gameTitle = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_GAME_BY_ID);
            preparedStatement.setLong(1, gameId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                gameTitle = resultSet.getString(TITLE);
            }
            preparedStatement.clearParameters();
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return gameTitle;
    }

    @Override
    public String findTitleBySetOfToursId(Long setOfToursId) throws DaoException {
        String gameTitle = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_GAME_TITLE_BY_SET_OF_TOURS_ID);
            preparedStatement.setLong(1, setOfToursId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                gameTitle = resultSet.getString(TITLE);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return gameTitle;
    }

    @Override
    public List<String> findAllTitles() throws DaoException {
        List<String> gameTitles = new ArrayList<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_GAMES);
            while (resultSet.next()) {
                gameTitles.add(resultSet.getString(TITLE));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return gameTitles;
    }

    @Override
    public List<Game> findAll() throws DaoException {
        List<Game> games = new ArrayList<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_GAMES);
            while (resultSet.next()) {
                Game game = new Game();
                game.setGameId(resultSet.getLong(GAME + UNDERSCORE + ID));
                game.setTitle(resultSet.getString(TITLE));
                game.setTours(findToursByGameId(game.getGameId()));
                games.add(game);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return games;
    }

    @Override
    public List<Game> updateAll(List<Game> entities) throws DaoException {
        List<Game> updatedGames = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_UPDATE_ALL_GAMES);
            for (Game game : entities) {
                preparedStatement.setLong(1, game.getGameId());
                preparedStatement.setString(2, game.getTitle());
                if (preparedStatement.executeUpdate() != 0) {
                    updatedGames.add(game);
                }
                preparedStatement.clearParameters();
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method updateAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return updatedGames;
    }

    @Override
    public List<Game> putAll(List<Game> entities) throws DaoException {
        List<Game> puttedGames = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_INSERT_ALL_GAMES);
            for (Game game : entities) {
                preparedStatement.setLong(1, game.getGameId());
                preparedStatement.setString(2, game.getTitle());
                if (preparedStatement.executeUpdate() != 0) {
                    puttedGames.add(game);
                }
                preparedStatement.clearParameters();
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method putAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return puttedGames;
    }

    @Override
    public List<Game> deleteAll(List<Game> entities) throws DaoException {
        List<Game> deletedGames = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_DELETE_ALL_GAMES);
            for (Game game : entities) {
                preparedStatement.setLong(1, game.getGameId());
                preparedStatement.setString(2, game.getTitle());
                if (preparedStatement.executeUpdate() != 0) {
                    deletedGames.add(game);
                }
                preparedStatement.clearParameters();
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method deleteAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return deletedGames;
    }

    @Override
    public List<Long> findAllToursIdByGameId(Long gameId) throws DaoException {
        List<Long> toursId = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_TOUR_ID_BY_GAME_ID);
            preparedStatement.setLong(1, gameId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                toursId.add(resultSet.getLong(TOUR + UNDERSCORE + ID));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAllToursIdByGameId", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return toursId;
    }

    @Override
    public List<Long> updateAllToursIdByGameId(Long gameId, List<Long> toursId) throws DaoException {
        List<Long> updatedToursId = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_UPDATE_TOUR_ID_BY_GAME_ID);
            preparedStatement.setLong(2, gameId);
            for (Long tourId : toursId) {
                preparedStatement.setLong(1, tourId);
                if (preparedStatement.executeUpdate() != 0) {
                    updatedToursId.add(tourId);
                }
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findSongsByTourId", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return updatedToursId;
    }

    @Override
    public List<Long> putAllToursIdByGameId(Long gameId, List<Long> toursId) throws DaoException {
        List<Long> puttedToursId = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_INSERT_TOUR_ID_BY_GAME_ID);
            preparedStatement.setLong(2, gameId);
            for (Long tourId : toursId) {
                preparedStatement.setLong(1, tourId);
                if (preparedStatement.executeUpdate() != 0) {
                    puttedToursId.add(tourId);
                }
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findSongsByTourId", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return puttedToursId;
    }

    @Override
    public List<Long> deleteAllToursIdByGameId(Long gameId, List<Long> toursId) throws DaoException {
        List<Long> deletedToursId = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_DELETE_TOUR_ID_BY_GAME_ID);
            preparedStatement.setLong(2, gameId);
            for (Long tourId : toursId) {
                preparedStatement.setLong(1, tourId);
                if (preparedStatement.executeUpdate() != 0) {
                    deletedToursId.add(tourId);
                }
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findSongsByTourId", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return deletedToursId;
    }

    @Override
    public Long findSetOfToursIdByGameIdAndTourId(Long gameId, Long tourId) throws DaoException {
        Long setOfToursId = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_SET_OF_TOURS_ID_BY_GAME_ID_AND_TOUR_ID);
            preparedStatement.setLong(1, gameId);
            preparedStatement.setLong(2, tourId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                setOfToursId = resultSet.getLong(SET + UNDERSCORE + OF + UNDERSCORE + TOURS + UNDERSCORE + ID);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return setOfToursId;
    }
}
