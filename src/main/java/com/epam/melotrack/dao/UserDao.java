package com.epam.melotrack.dao;

import com.epam.melotrack.entity.User;
import com.epam.melotrack.exception.DaoException;

public interface UserDao extends Dao<User> {

    User findByUsername(String username) throws DaoException;

    @Override
    User findById(long userId) throws DaoException;

    Long findIdByUsername(String username) throws DaoException;

}
