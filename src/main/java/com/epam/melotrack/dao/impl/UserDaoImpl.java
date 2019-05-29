package com.epam.melotrack.dao.impl;

import com.epam.melotrack.dao.UserDao;
import com.epam.melotrack.entity.User;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.pool.ConnectionPool;
import com.epam.melotrack.pool.ProxyConnection;
import com.epam.melotrack.service.Scrambler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.melotrack.service.Service.*;

public class UserDaoImpl implements UserDao {

    private final static Logger logger = LogManager.getLogger();
    private static ConnectionPool connectionPool;

    public final static String SQL_SELECT_ALL_USERS = "SELECT user_id, username, password, role FROM users";
    public final static String SQL_UPDATE_ALL_USERS = "UPDATE INTO users SET user_id = ?, username = ?, password = ?, role = ?";
    public final static String SQL_INSERT_ALL_USERS = "INSERT INTO users (user_id, username, password, role) VALUES (?,?,?,?)";
    public final static String SQL_DELETE_ALL_USERS = "DELETE FROM users WHERE user_id = ? AND username = ? AND password = ? AND role = ?";

    public final static String SQL_SELECT_USER_BY_ID = "SELECT user_id, username, password, role FROM users WHERE user_id = ?";
    public final static String SQL_SELECT_USER_BY_USERNAME = "SELECT user_id, username, password, role FROM users WHERE username = ?";

    public UserDaoImpl() {
        connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public User findByUsername(String username) throws DaoException {
        User user = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_USER_BY_USERNAME);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setUserId(resultSet.getLong(USER + UNDERSCORE + ID));
                user.setUserName(resultSet.getString(USERNAME));
                user.setPassword(resultSet.getString(PASSWORD));
                user.setRole(resultSet.getString(ROLE));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findByUsername", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return user;
    }

    @Override
    public User findById(long userId) throws DaoException {
        User user = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_USER_BY_ID);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setUserId(resultSet.getLong(USER + UNDERSCORE + ID));
                user.setUserName(resultSet.getString(USERNAME));
                user.setPassword(resultSet.getString(PASSWORD));
                user.setRole(resultSet.getString(ROLE));
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findById", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return user;
    }

    @Override
    public Long findIdByUsername(String username) throws DaoException {
        Long userId = null;
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_USER_BY_USERNAME);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getLong(USER + UNDERSCORE + ID);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findByUsername", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
        return userId;
    }

    @Override
    public List<User> findAll() throws DaoException {
        List<User> users = new ArrayList<>();
        ProxyConnection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_USERS);
            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getLong(USER + UNDERSCORE + ID));
                user.setUserName(resultSet.getString(USERNAME));
                user.setPassword(resultSet.getString(PASSWORD));
                user.setRole(resultSet.getString(ROLE));
                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method findAll", e);
        } finally {
            close(resultSet, statement, connection);
        }
        return users;
    }

    @Override
    public List<User> updateAll(List<User> entities) throws DaoException {
        List<User> updatedUsers = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_UPDATE_ALL_USERS);
            for (User user : entities) {
                preparedStatement.setLong(1, user.getUserId());
                preparedStatement.setString(2, user.getUserName());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setString(4, user.getRole());
                if (preparedStatement.executeUpdate() != 0) {
                    updatedUsers.add(user);
                }
                preparedStatement.clearParameters();
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method updateAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return updatedUsers;
    }

    @Override
    public List<User> putAll(List<User> entities) throws DaoException {
        List<User> putedUsers = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_INSERT_ALL_USERS);
            for (User user : entities) {
                preparedStatement.setLong(1, user.getUserId());
                preparedStatement.setString(2, user.getUserName());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setString(4, user.getRole());
                if (preparedStatement.executeUpdate() != 0) {
                    putedUsers.add(user);
                }
                preparedStatement.clearParameters();
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method putAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return putedUsers;
    }

    @Override
    public List<User> deleteAll(List<User> entities) throws DaoException {
        List<User> deletedUsers = new ArrayList<>();
        ProxyConnection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(SQL_DELETE_ALL_USERS);
            for (User user : entities) {
                preparedStatement.setLong(1, user.getUserId());
                preparedStatement.setString(2, user.getUserName());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setString(4, user.getRole());
                if (preparedStatement.executeUpdate() != 0) {
                    deletedUsers.add(user);
                }
                preparedStatement.clearParameters();
            }
        } catch (SQLException e) {
            logger.error("Database statement creating error!", e);
            throw new DaoException("Database statement creating error in method deleteAll", e);
        } finally {
            close(null, preparedStatement, connection);
        }
        return deletedUsers;
    }
}
