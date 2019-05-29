package com.epam.melotrack.validation;

public class UserValidatior {

    public final static String USERNAME_REGEX = "^[\\w]{5,30}$";
    public final static String PASSWORD_REGEX = "^[\\w]{5,30}$";

    private UserValidatior(){}

    public static boolean isValidUserName(String username) {
        return (username != null && username.matches(USERNAME_REGEX));
    }

    public static boolean isValidPassword(String password) {
        return (password != null && password.matches(PASSWORD_REGEX));
    }

}
