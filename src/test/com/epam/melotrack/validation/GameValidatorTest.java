package com.epam.melotrack.validation;

import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class GameValidatorTest {

    @Test
    public void testIsValidGameTitle() {
        String gameTitle = "<script>alert('Hello')</script>";
        Assert.assertFalse(GameValidator.isValidGameTitle(gameTitle));
    }

    @Test
    public void testIsValidToursTitles() {
        List<String> tourTitles = List.of("title", "other title", "title");
        Assert.assertFalse(GameValidator.isValidToursTitles(tourTitles));
    }
}