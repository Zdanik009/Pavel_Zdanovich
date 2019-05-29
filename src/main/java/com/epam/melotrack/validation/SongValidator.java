package com.epam.melotrack.validation;

import java.util.Calendar;
import java.util.Optional;

public class SongValidator {

    public final static String MUSICIAN_REGEX = "^[\\w\\s\\{\\}\\\"\\'\\/\\|\\;\\:\\.\\,\\~\\!\\?\\@\\#\\$\\€\\¥\\£\\¢\\%\\^\\-\\+\\=\\&\\*\\[\\]\\(\\)]{1,45}$";
    public final static String TITLE_REGEX = "^[\\w\\s\\{\\}\\\"\\'\\/\\|\\;\\:\\.\\,\\~\\!\\?\\@\\#\\$\\€\\¥\\£\\¢\\%\\^\\-\\+\\=\\&\\*\\[\\]\\(\\)]{1,45}$";
    public final static String GENRE_REGEX = "^[\\w\\s]{0,45}$";
    public final static String ALBUM_REGEX = "^[\\w\\s]{0,45}$";
    public final static String INFORMATION_REGEX = "^[\\w\\s]{0,65000}$";
    public final static String DATE_REGEX = "^[\\d]{4}$";
    public final static short DEFAULT_START_DATE = 1900;
    public final static short DEFAULT_CURRENT_DATE;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DEFAULT_CURRENT_DATE = (short) calendar.get(Calendar.YEAR);
    }

    private SongValidator() {
    }

    public static boolean isValidMusician(String musician) {
        return (musician != null && musician.matches(MUSICIAN_REGEX));
    }

    public static boolean isValidTitle(String title) {
        return (title != null && title.matches(TITLE_REGEX));
    }

    public static boolean isValidGenre(String genre) {
        return (genre != null && genre.matches(GENRE_REGEX));
    }

    public static boolean isValidAlbum(String album) {
        return (album != null && album.matches(ALBUM_REGEX));
    }

    public static boolean isValidInformation(String information) {
        return (information != null && information.matches(INFORMATION_REGEX));
    }

    public static boolean isValidDate(String date) {
        boolean result;
        if (date != null) {
            if (date.matches(DATE_REGEX)) {
                try {
                    short shortDate = Short.parseShort(date);
                    result = (shortDate >= DEFAULT_START_DATE && shortDate <= DEFAULT_CURRENT_DATE);
                } catch (NumberFormatException e) {
                    result = false;
                }
            } else {
                result = date.isEmpty();
            }
        } else {
            result = false;
        }
        return result;
    }

}
