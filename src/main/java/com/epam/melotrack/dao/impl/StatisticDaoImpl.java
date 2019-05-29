package com.epam.melotrack.dao.impl;

import com.epam.melotrack.dao.StatisticDao;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.pool.ConnectionPool;
import com.epam.melotrack.pool.ProxyConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.epam.melotrack.service.Service.*;

public class StatisticDaoImpl implements StatisticDao {

    private final static Logger logger = LogManager.getLogger();
    private static ConnectionPool connectionPool = ConnectionPool.getInstance();

    public final static String SQL_SELECT_ALL_STATISTICS = "SELECT user_id, set_of_tours_id, date, result FROM statistics";
    public final static String SQL_UPDATE_ALL_STATISTICS = "UPDATE INTO statistics SET user_id = ?, set_of_tours_id = ?, date = ?, result = ?";
    public final static String SQL_INSERT_ALL_STATISTICS = "INSERT INTO statistics (user_id, set_of_tours_id, date, result) VALUES (?,?,?,?)";
    public final static String SQL_DELETE_ALL_STATISTICS = "DELETE FROM statistics WHERE user_id = ? AND set_of_tours_id = ? AND date = ? AND result = ?";

    public final static String SQL_SELECT_STATISTIC_BY_USER_ID = "SELECT user_id, set_of_tours_id, date, result FROM statistics WHERE user_id = ?";
    public final static String SQL_SELECT_STATISTIC_BY_SET_OF_TOURS_ID = "SELECT user_id, set_of_tours_id, date, result FROM statistics WHERE set_of_tours_id = ?";

    @Override
    public Statistic findByUserId(long userId) throws DaoException {
        Statistic statistic = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_STATISTIC_BY_USER_ID);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                statistic = new Statistic();
                statistic.setUserId(userId);
                do {
                    statistic.put(resultSet.getLong(SET + UNDERSCORE + OF + UNDERSCORE + TOURS + UNDERSCORE + ID),
                                                 resultSet.getByte(RESULT),
                                                 resultSet.getTimestamp(DATE));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return statistic;
    }

    @Override
    public Statistic findBySetOfToursId(long setOfToursId) throws DaoException {
        Statistic statistic = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_STATISTIC_BY_SET_OF_TOURS_ID);
            preparedStatement.setLong(1, setOfToursId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                statistic = new Statistic();
                statistic.setUserId(resultSet.getLong(USER + UNDERSCORE + ID));
                do {
                    statistic.put(setOfToursId, resultSet.getByte(RESULT), resultSet.getTimestamp(DATE));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return statistic;
    }

    @Deprecated
    public Statistic findById(long id)  {
        throw new  UnsupportedOperationException();
    }

    @Override
    public List<Statistic> findAll() throws DaoException {
        Set<Statistic> statistics = new HashSet<>();
        Map<Long, Statistic> correspondence = new HashMap<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_STATISTICS);
            while (resultSet.next()) {
                long userId = resultSet.getLong(USER + UNDERSCORE + ID);
                Statistic statistic;
                if ((statistic = correspondence.get(userId)) != null) {
                    statistic.put(resultSet.getLong(SET + UNDERSCORE + OF + UNDERSCORE + TOURS + UNDERSCORE + ID),
                                  resultSet.getByte(RESULT),
                                  resultSet.getTimestamp(DATE));
                } else {
                    statistic = new Statistic();
                    statistic.setUserId(userId);
                    statistic.put(resultSet.getLong(SET + UNDERSCORE + OF + UNDERSCORE + TOURS + UNDERSCORE + ID),
                                  resultSet.getByte(RESULT),
                                  resultSet.getTimestamp(DATE));
                    correspondence.put(userId, statistic);
                }
                statistics.add(statistic);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return new ArrayList<>(statistics);
    }

    @Override
    public List<Statistic> updateAll(List<Statistic> entities) throws DaoException {
        List<Statistic> updatedStatistics = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_UPDATE_ALL_STATISTICS);
            for (Statistic statistic : entities) {
                for (long setOfToursId : statistic.getSetsOfToursId()) {
                    preparedStatement.setLong(1, statistic.getUserId());
                    preparedStatement.setLong(2, setOfToursId);
                    preparedStatement.setTimestamp(3, statistic.getDateBySetOfToursId(setOfToursId));
                    preparedStatement.setByte(4, statistic.getResultBySetOfToursId(setOfToursId));
                    if(preparedStatement.executeUpdate() != 0) {
                        updatedStatistics.add(statistic);
                    }
                    preparedStatement.clearParameters();
                }
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return updatedStatistics;
    }

    @Override
    public List<Statistic> putAll(List<Statistic> entities) throws DaoException {
        List<Statistic> putedStatistics = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_INSERT_ALL_STATISTICS);
            for (Statistic statistic : entities) {
                for (long setOfToursId : statistic.getSetsOfToursId()) {
                    preparedStatement.setLong(1, statistic.getUserId());
                    preparedStatement.setLong(2, setOfToursId);
                    preparedStatement.setTimestamp(3, statistic.getDateBySetOfToursId(setOfToursId));
                    preparedStatement.setByte(4, statistic.getResultBySetOfToursId(setOfToursId));
                    if(preparedStatement.executeUpdate() != 0) {
                        putedStatistics.add(statistic);
                    }
                    preparedStatement.clearParameters();
                }
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return putedStatistics;
    }

    @Override
    public List<Statistic> deleteAll(List<Statistic> entities) throws DaoException {
        List<Statistic> deletedStatistics = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_DELETE_ALL_STATISTICS);
            for (Statistic statistic : entities) {
                for (long setOfToursId : statistic.getSetsOfToursId()) {
                    preparedStatement.setLong(1, statistic.getUserId());
                    preparedStatement.setLong(2, setOfToursId);
                    preparedStatement.setTimestamp(3, statistic.getDateBySetOfToursId(setOfToursId));
                    preparedStatement.setByte(4, statistic.getResultBySetOfToursId(setOfToursId));
                    if(preparedStatement.executeUpdate() != 0) {
                        deletedStatistics.add(statistic);
                    }
                    preparedStatement.clearParameters();
                }
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return deletedStatistics;
    }
}
