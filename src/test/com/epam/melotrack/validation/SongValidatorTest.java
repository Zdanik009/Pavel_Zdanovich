package com.epam.melotrack.validation;

import org.junit.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SongValidatorTest {

    @Test
    public void testIsValidMusician() {
        String musician = "<script>alert('Hello')</script>";
        Assert.assertFalse(SongValidator.isValidMusician(musician));
    }

    @Test
    public void testIsValidTitle() {
        String title = "<script>alert('Hello')</script>";
        Assert.assertFalse(SongValidator.isValidTitle(title));
    }

    @Test
    public void testIsValidGenre() {
        String genre = "<script>alert('Hello')</script>";
        Assert.assertFalse(SongValidator.isValidGenre(genre));
    }

    @Test
    public void testIsValidAlbum() {
        String album = "<script>alert('Hello')</script>";
        Assert.assertFalse(SongValidator.isValidAlbum(album));
    }

    @Test
    public void testIsValidInformation() {
        String information = "<script>alert('Hello')</script>";
        Assert.assertFalse(SongValidator.isValidInformation(information));
    }

    @Test
    public void testIsValidDate() {
        String date = "date";
        Assert.assertFalse(SongValidator.isValidDate(date));
    }
}