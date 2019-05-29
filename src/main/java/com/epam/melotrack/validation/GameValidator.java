package com.epam.melotrack.validation;

import com.epam.melotrack.dao.impl.TourDaoImpl;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.exception.DaoException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameValidator {

    public final static String GAME_TITLE_REGEX = "^[a-zA-Z0-9]{1,30}$";

    private GameValidator() {}

    public static boolean isValidGameTitle(String gameTitle) {
        return (gameTitle != null && gameTitle.matches(GAME_TITLE_REGEX));
    }

    public static boolean isValidToursTitles(List<String> toursTitles) {
        if (toursTitles != null && !toursTitles.isEmpty()) {
            Set<String> uniqueTitles = new HashSet<>(toursTitles);
            return uniqueTitles.size() == Game.DEFAULT_GAME_TOURS_AMOUNT;
        } else {
            return false;
        }
    }

}
