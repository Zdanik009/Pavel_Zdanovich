package com.epam.melotrack.command;

import java.util.Optional;

public enum CommandType {

    PREPARE_PAGE,
    SHOW_MAIN_PAGE,
    SHOW_LOGIN_PAGE,
    SHOW_UPLOAD_GAME_PAGE,
    SHOW_UPLOAD_TOUR_PAGE,
    SHOW_UPLOAD_SONG_PAGE,
    SHOW_ABOUT_PAGE,
    SIGN_UP_USER,
    SIGN_IN_USER,
    LOGOUT_USER,
    LOAD_GAME,
    LOAD_TOUR,
    UPLOAD_GAME,
    UPLOAD_TOUR,
    UPLOAD_SONG,
    LOAD_COMMON_STATISTIC,
    LOAD_USER_STATISTIC,
    CHANGE_LANGUAGE,
    START_TOUR,
    SUBMIT_TOUR,
    NEXT_TOUR,
    LEAVE_PLAYGROUND;

    public static CommandType stringValueOf(String command){
        return Optional.of(valueOf(command.toUpperCase())).orElse(SHOW_MAIN_PAGE);
    }

}
