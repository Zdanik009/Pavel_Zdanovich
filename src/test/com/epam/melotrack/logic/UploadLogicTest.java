package com.epam.melotrack.logic;

import com.epam.melotrack.entity.*;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.Assert.*;

public class UploadLogicTest {

    @Test
    public void testUploadStatistic() {
        Statistic statistic = new Statistic();
        Statistic uploadedStatistic = UploadLogic.uploadStatistic(statistic);
        Assert.assertNull(uploadedStatistic);
    }

    @Test
    public void testUploadUser() {
        User user = new User();
        User uploadedUser = UploadLogic.uploadUser(user);
        Assert.assertNull(uploadedUser);
    }

    @Test
    public void testUploadGame() {
        String gameTitle = "title";
        List<String> tourTitles = List.of("title", "other title", "title");
        Game uploadedGame = UploadLogic.uploadGame(gameTitle, tourTitles);
        Assert.assertNull(uploadedGame);
    }

    @Test
    public void testUploadTour() {
        String tourTitle = "title";
        List<String> songTitles = List.of("title", "other title", "title");
        String time = "-1";
        Tour tour = UploadLogic.uploadTour(tourTitle, songTitles, time);
        Assert.assertNull(tour);
    }

    @Test
    public void testUploadSong() {
        String musician = "musician";
        String title = "title";
        String genre = "<script>alert('Hello')</script>";
        String album = "";
        String date = "-1";
        String information = null;
        File file = null;
        Song song = UploadLogic.uploadSong(musician, title, genre, album, date, information, file);
        Assert.assertNull(song);
    }
}