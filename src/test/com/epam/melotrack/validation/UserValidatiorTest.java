package com.epam.melotrack.validation;

import org.junit.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UserValidatiorTest {

    @Test
    public void testIsValidUserName() {
        String username = "<script>alert('Hello')</script>";
        Assert.assertFalse(UserValidatior.isValidUserName(username));
    }

    @Test
    public void testIsValidPassword() {
        String password = "<script>alert('Hello')</script>";
        Assert.assertFalse(UserValidatior.isValidPassword(password));
    }
}