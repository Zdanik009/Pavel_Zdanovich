package com.epam.melotrack.validation;

import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class TourValidatorTest {

    @Test
    public void testIsValidTourTitle() {
        String tourTitle = "<script>alert('Hello')</script>";
        Assert.assertFalse(TourValidator.isValidTourTitle(tourTitle));
    }

    @Test
    public void testIsValidSongsTitles() {
        List<String> songTitles = List.of("title", "other title", "title");
        Assert.assertFalse(TourValidator.isValidSongsTitles(songTitles));
    }

    @Test
    public void testIsValidTime() {
        String time = "time";
        Assert.assertFalse(TourValidator.isValidTime(time));
    }
}