package com.epam.melotrack.logic;

import com.epam.melotrack.dao.TourDao;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.entity.User;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class LogicTest {

    @Test
    public void testChangeLanguage() {
        String avaliableLocale = "be_by";
        AvaliableLocale expected = AvaliableLocale.BE_BY;
        AvaliableLocale actual = Logic.changeLanguage(avaliableLocale);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testLogin() {
        String username = "ADMIN";
        String password = "progmathist";
        User user = Logic.login(username, password);
        Assert.assertNotNull(user);
    }

    @Test
    public void testRegister() {
        String username = "ADMIN";
        String password = "progmathist";
        User user = Logic.register(username, password);
        Assert.assertNull(user);
    }

    @Test
    public void testNextTour() {
        Game game = new Game();
        Tour tour = new Tour();
        Tour nextTour = Logic.nextTour(game, tour);
        Assert.assertNull(nextTour);
    }

    @Test
    public void testPrepareTour() {
        Tour tour = new Tour();
        Tour preparedTour = Logic.prepareTour(tour);
        Assert.assertEquals(tour, preparedTour);
    }

    @Test
    public void testCheckTour() {
        Tour tour = new Tour();
        Map<String, List<String>> userAnswers = new HashMap<>();
        int categories = 0;
        byte expected = 0;
        byte actual = Logic.checkTour(tour, userAnswers, categories);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testLeavePlayground() {
        User user = new User();
        Statistic statistic = new Statistic();
        Game game = new Game();
        Tour tour = new Tour();
        String gameStatus = "LOADED";
        String tourStatus = "LOADED";
        Statistic uploadedStatistic = Logic.leavePlayground(user, statistic, game, tour, gameStatus, tourStatus);
        Assert.assertEquals(statistic, uploadedStatistic);
    }

    @Test
    public void testRecordStatistic() {
        User user = new User();
        Statistic statistic = new Statistic();
        Game game = new Game();
        Tour tour = new Tour();
        byte result = 0;
        Statistic recordedStatistic = Logic.recordStatistic(user, statistic, game, tour, result);
        Assert.assertEquals(statistic, recordedStatistic);
    }

}