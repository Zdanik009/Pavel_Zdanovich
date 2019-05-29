package com.epam.melotrack.dao;

import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.exception.DaoException;

import java.util.List;

public interface StatisticDao extends Dao<Statistic> {

    Statistic findByUserId(long userId) throws DaoException;

    Statistic findBySetOfToursId(long setOfToursId) throws DaoException;

    @Deprecated
    Statistic findById(long id) throws DaoException;

}
