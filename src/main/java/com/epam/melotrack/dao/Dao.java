package com.epam.melotrack.dao;

import com.epam.melotrack.entity.Entity;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.pool.ProxyConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface Dao<E extends Entity> {

    E findById(long id) throws DaoException;

    List<E> findAll() throws DaoException;

    List<E> updateAll(List<E> entities) throws DaoException;

    List<E> putAll(List<E> entities) throws DaoException;

    List<E> deleteAll(List<E> entities) throws DaoException;

    default boolean close(ResultSet resultSet, Statement statement, ProxyConnection connection) {
        try {
            if(resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
            if(statement != null && !statement.isClosed()) {
                statement.close();
            }
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    };

}
