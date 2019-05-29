package com.epam.melotrack.service;

import org.junit.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ScramblerTest {

    @Test
    public void testEncrypt() {
        String string = null;
        String encryptedString = Scrambler.encrypt(string);
        Assert.assertNull(encryptedString);
    }
}