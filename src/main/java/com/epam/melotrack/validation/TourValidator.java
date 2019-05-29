package com.epam.melotrack.validation;

import com.epam.melotrack.entity.Tour;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TourValidator {

    public final static String TOUR_TITLE_REGEX = "^[a-zA-Z0-9]{1,30}$";
    public final static String TIME_REGEX = "^[\\d]{1,2}$";
    public final static byte DEFAULT_MIN_TIME = 5;
    public final static byte DEFAULT_MAX_TIME = 15;

    private TourValidator() {
    }

    public static boolean isValidTourTitle(String tourTitle) {
        return (tourTitle != null && tourTitle.matches(TOUR_TITLE_REGEX));
    }

    public static boolean isValidSongsTitles(List<String> songsTitles) {
        if (songsTitles != null) {
            Set<String> uniqueTitles = new HashSet<>(songsTitles);
            return uniqueTitles.size() == Tour.DEFAULT_TOUR_SONGS_AMOUNT;
        } else {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        boolean result;
        if (time != null) {
            if (time.matches(TIME_REGEX)) {
                try {
                    byte shortDate = Byte.parseByte(time);
                    result = (shortDate >= DEFAULT_MIN_TIME && shortDate <= DEFAULT_MAX_TIME);
                } catch (NumberFormatException e) {
                    result = false;
                }
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

}
