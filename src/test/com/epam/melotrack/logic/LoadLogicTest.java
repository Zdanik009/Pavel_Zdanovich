package com.epam.melotrack.logic;

import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.entity.User;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class LoadLogicTest {

    @Test
    public void testFindUserStatistic() {
        User user = new User();
        Statistic statistic = LoadLogic.findUserStatistic(user);
        Assert.assertNull(statistic);
    }

    @Test
    public void testFindGameByTitle() {
        String gameTitle = "<script>alert('Hello');</script>";
        Game game = LoadLogic.findGameByTitle(gameTitle);
        Assert.assertNull(game);
    }

    @Test
    public void testFindTourByTitle() {
        String tourTitle = null;
        Tour tour = LoadLogic.findTourByTitle(tourTitle);
        Assert.assertNull(tour);
    }

    @Test
    public void testFindSongsIdByTitles() {
        List<String> songsTitles = new ArrayList<>();
        List<Long> songsId = LoadLogic.findSongsIdByTitles(songsTitles);
        Assert.assertNull(songsId);
    }

    @Test
    public void testFindGameAndToursTitlesOfStatistic() {
        Statistic statistic = new Statistic();
        List<String> gameAndTourTitles = LoadLogic.findGameAndToursTitlesOfStatistic(statistic);
        Assert.assertNull(gameAndTourTitles);
    }

    @Test
    public void testFindTourIdByTitle() {
        String tourTitle = null;
        Long tourId = LoadLogic.findTourIdByTitle(tourTitle);
        Assert.assertNull(tourId);
    }

    @Test
    public void testFindToursIdByTitles() {
        List<String> toursTitles = List.of("", "<script>alert('Hello');</script>");
        List<Long> toursId = LoadLogic.findToursIdByTitles(toursTitles);
        Assert.assertNotNull(toursId);
    }

    @Test
    public void testFindAllGameTitlesForUser() {
        User user = new User();
        Statistic statistic = new Statistic();
        List<String> gameTitles = LoadLogic.findAllGameTitlesForUser(user, statistic);
        Assert.assertNull(gameTitles);
    }

    @Test
    public void testFindGamesIdByTitles() {
        List<String> gameTitles = List.of("", "<script>alert('Hello');</script>");
        List<Long> gamesId = LoadLogic.findGamesIdByTitles(gameTitles);
        Assert.assertNotNull(gamesId);
    }

    @Test
    public void testFindUserByUsername() {
        String username = null;
        User user = LoadLogic.findUserByUsername(username);
        Assert.assertNull(user);
    }
}