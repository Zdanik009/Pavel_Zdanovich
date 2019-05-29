package com.epam.melotrack.logic;

import com.epam.melotrack.dao.*;
import com.epam.melotrack.dao.impl.*;
import com.epam.melotrack.entity.*;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.servlet.RequestContent;
import com.epam.melotrack.validation.GameValidator;
import com.epam.melotrack.validation.SongValidator;
import com.epam.melotrack.validation.TourValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

import static com.epam.melotrack.service.Service.*;

public class UploadLogic {

    private final static Logger logger = LogManager.getLogger();
    public final static String CONTENT_PROPERTIES_FILE_PATH = "content.properties";
    public final static String CONTENT_PROPERTIES_NAME_REGEX = "^[a-zA-Z0-9]{1,30}$";

    private UploadLogic() {
    }

    private static boolean addToResourceBundle(String name) {
        boolean result = false;
        if (name != null && name.matches(CONTENT_PROPERTIES_NAME_REGEX)) {
            Properties properties = new Properties();
            File file = new File(RequestContent.getServletContextRealPath() + SLASH + WEB_INF + SLASH + CLASSES, CONTENT_PROPERTIES_FILE_PATH);
            try (InputStream inputStream = new FileInputStream(file);OutputStream outputStream = new FileOutputStream(file, true)) {
                properties.setProperty(LABEL + DOT + name, name);
                properties.store(outputStream, null);
                properties.load(inputStream);
                logger.info(properties.getProperty(LABEL + DOT + name));
                logger.info(properties.get(LABEL + DOT + RESULT));
                result = true;
            } catch (FileNotFoundException e) {
                logger.error("Unable to write to resource bundle due to ", e);
            } catch (IOException e) {
                logger.error("Writing to resource bundle error due to ", e);
            }
        }
        return result;
    }

    public static Statistic uploadStatistic(Statistic statistic) {
        Statistic uploadedStatistic = null;
        if (isValidStatistic(statistic)) {
            try {
                StatisticDao statisticDao = new StatisticDaoImpl();
                uploadedStatistic = statisticDao.putAll(List.of(statistic)).get(0);
                logger.info("Statistic : " + statistic + " uploaded");
                if (uploadedStatistic == null || !uploadedStatistic.equals(statistic)) {
                    logger.error("Uploading statistic error!");
                    removeStatistic(statistic);
                    uploadedStatistic = null;
                }
            } catch (DaoException e) {
                logger.error("Error in statistic dao due to ", e);
            }
        }
        return uploadedStatistic;
    }

    private static List<Statistic> removeStatistic(Statistic statistic) {
        List<Statistic> deletedStatistics = null;
        try {
            StatisticDao statisticDao = new StatisticDaoImpl();
            deletedStatistics = statisticDao.deleteAll(List.of(statistic));
        } catch (DaoException e) {
            logger.error("Error in user dao due to ", e);
        }
        return deletedStatistics;
    }

    public static User uploadUser(User user) {
        User uploadedUser = null;
        if (user != null && user.getUserName() != null && user.getPassword() != null && user.getRole() != null) {
            try {
                UserDao userDao = new UserDaoImpl();
                uploadedUser = userDao.putAll(List.of(user)).get(0);
                uploadedUser.setUserId(userDao.findIdByUsername(user.getUserName()));
                if (uploadedUser == null || !uploadedUser.equals(user)) {
                    logger.error("Uploading user error!");
                    removeUser(uploadedUser);
                    uploadedUser = null;
                }
            } catch (DaoException e) {
                logger.error("Error in user dao due to ", e);
            }
        } else {
            logger.error("Invalid user : " + user);
        }
        return uploadedUser;
    }

    private static List<User> removeUser(User user) {
        List<User> deletedUsers = null;
        try {
            UserDao userDao = new UserDaoImpl();
            deletedUsers = userDao.deleteAll(List.of(user));
        } catch (DaoException e) {
            logger.error("Error in user dao due to ", e);
        }
        return deletedUsers;
    }

    public static Game uploadGame(String gameTitle, List<String> tourTitles) {
        Game game = null;
        if (isValidGameDetails(gameTitle, tourTitles)) {
            game = new Game();
            game.setTitle(gameTitle);
            Game uploadedGame = putAllGames(List.of(game)).get(0);
            uploadedGame.setGameId(LoadLogic.findGamesIdByTitles(List.of(uploadedGame.getTitle())).get(0));
            List<Long> songsId = LoadLogic.findToursIdByTitles(tourTitles);
            List<Long> uploadedSongsId = putAllToursIdByGameId(game.getGameId(), songsId);
            if (uploadedGame != null && uploadedGame.equals(game) && uploadedSongsId != null && uploadedSongsId.equals(songsId)) {
                addToResourceBundle(uploadedGame.getTitle());
                game = uploadedGame;
            } else {
                logger.error("Uploading game error!");
                removeGame(uploadedGame, songsId);
                game = null;
            }
        }
        return game;
    }

    private static List<Game> removeGame(Game game, List<Long> toursId) {
        List<Game> deletedGames = null;
        try {
            GameDao gameDao = new GameDaoImpl();
            deletedGames = gameDao.deleteAll(List.of(game));
            gameDao.deleteAllToursIdByGameId(game.getGameId(), toursId);
        } catch (DaoException e) {
            logger.error("Error in game dao due to ", e);
        }
        return deletedGames;
    }

    private static List<Game> putAllGames(List<Game> games) {
        List<Game> uploadedGames = null;
        try {
            GameDao gameDao = new GameDaoImpl();
            uploadedGames = gameDao.putAll(games);
        } catch (DaoException e) {
            logger.error("Error in game dao due to ", e);
        }
        return uploadedGames;
    }

    private static List<Long> putAllToursIdByGameId(long gameId, List<Long> toursId) {
        List<Long> uploadedToursId = null;
        try {
            GameDao gameDao = new GameDaoImpl();
            uploadedToursId = gameDao.putAllToursIdByGameId(gameId, toursId);
        } catch (DaoException e) {
            logger.error("Error in tour dao due to ", e);
        }
        return uploadedToursId;
    }

    public static Tour uploadTour(String tourTitle, List<String> songTitles, String time) {
        Tour tour = null;
        if (isValidTourDetails(tourTitle, songTitles, time)) {
            tour = new Tour();
            tour.setTitle(tourTitle);
            tour.setTime(Byte.parseByte(time));
            File file = LoadLogic.findTemplateTourRules();
            tour.setRules(file);
            tour.setType(Tour.TYPE_SIMPLE);
            tour.setCategories(List.of(MUSICIAN, TITLE));
            Tour uploadedTour = putAllTours(List.of(tour)).get(0);
            tour.setTourId(LoadLogic.findTourIdByTitle(uploadedTour.getTitle()));
            Map<Long, String> songsIdAndPlayback = new HashMap<>(songTitles.size());
            List<Long> songsId = LoadLogic.findSongsIdByTitles(songTitles);
            songsId.forEach(songId -> songsIdAndPlayback.put(songId, Song.DEFAULT_SONG_PLAYBACK_INFO));
            Map<Long, String> uploadedSongsIdAndPlayback = putAllSongsIdByTourId(tour.getTourId(), songsIdAndPlayback);
            if (uploadedTour != null && uploadedTour.equals(tour) && uploadedSongsIdAndPlayback != null && uploadedSongsIdAndPlayback.equals(songsIdAndPlayback)) {
                addToResourceBundle(uploadedTour.getTitle());
                tour = uploadedTour;
            } else {
                logger.error("Uploading tour error!");
                removeTour(tour, songsIdAndPlayback);
                tour = null;
            }
        }
        return tour;
    }

    private static List<Tour> removeTour(Tour tour, Map<Long, String> songs) {
        List<Tour> deletedTours = null;
        try {
            TourDao tourDao = new TourDaoImpl();
            deletedTours = tourDao.deleteAll(List.of(tour));
            tourDao.deleteAllSongsIdAndPlaybackByTourId(tour.getTourId(), songs);
        } catch (DaoException e) {
            logger.error("Error in game dao due to ", e);
        }
        return deletedTours;
    }

    private static List<Tour> putAllTours(List<Tour> tours) {
        List<Tour> uploadedTours = new ArrayList<>();
        try {
            TourDao tourDao = new TourDaoImpl();
            uploadedTours = tourDao.putAll(tours);
        } catch (DaoException e) {
            logger.error("Error in tour dao due to ", e);
        }
        return uploadedTours;
    }

    private static Map<Long, String> putAllSongsIdByTourId(long tourId, Map<Long, String> songsId) {
        Map<Long, String> uploadedSongsId = null;
        try {
            TourDao tourDao = new TourDaoImpl();
            uploadedSongsId = tourDao.putAllSongsIdAndPlaybackByTourId(tourId, songsId);
        } catch (DaoException e) {
            logger.error("Error in tour dao due to ", e);
        }
        return uploadedSongsId;
    }

    public static Song uploadSong(String musician, String title, String genre, String album, String date, String information, File file) {
        Song song = null;
        if (isValidRequiredSongDetails(musician, title, file) && isValidOptionalSongDetails(genre, album, date, information)) {
            song = new Song();
            song.setMusician(musician);
            song.setTitle(title);
            song.setFile(file);
            song.setGenre(genre);
            song.setAlbum(album);
            song.setDate(Short.parseShort(date));
            song.setInformation(information);
            Song uploadedSong = putAllSongs(List.of(song)).get(0);
            song.setSongId(LoadLogic.findSongsIdByTitles(List.of(song.getMusician().concat(DASH).concat(song.getTitle()))).get(0));
            if (uploadedSong != null && uploadedSong.equals(song)) {
                song = uploadedSong;
            } else {
                logger.error("Uploading song error!");
                removeSong(song);
            }
        }
        return song;
    }

    private static List<Song> removeSong(Song song) {
        List<Song> deletedSongs = null;
        try {
            SongDao songDao = new SongDaoImpl();
            deletedSongs = songDao.deleteAll(List.of(song));
        } catch (DaoException e) {
            logger.error("Error in game dao due to ", e);
        }
        return deletedSongs;
    }

    private static List<Song> putAllSongs(List<Song> songs) {
        List<Song> uploadedSongs = null;
        try {
            SongDao songDao = new SongDaoImpl();
            uploadedSongs = songDao.putAll(songs);
        } catch (DaoException e) {
            logger.error("Error in game dao due to ", e);
        }
        return uploadedSongs;
    }

    private static boolean isValidStatistic(Statistic statistic) {
        boolean result = false;
        if (statistic != null && statistic.getSetsOfToursId() != null && !statistic.getSetsOfToursId().isEmpty()) {
            try {
                StatisticDao statisticDao = new StatisticDaoImpl();
                Statistic userStatistic = statisticDao.findByUserId(statistic.getUserId());
                if (userStatistic != null) {
                    List<Long> setsOfToursId = statistic.getSetsOfToursId();
                    List<Long> userSetsOfToursId = userStatistic.getSetsOfToursId();
                    if (setsOfToursId.stream().anyMatch(id -> userSetsOfToursId.contains(id))) {
                        logger.error("User has already statistic : " + userStatistic);
                    } else {
                        result = true;
                    }
                } else {
                    result = true;
                }
            } catch (DaoException e) {
                logger.error("Statistic dao error due to ", e);
            }
        } else {
            logger.error("Invalid statistic : " + statistic);
        }
        return result;
    }

    private static boolean isValidTourDetails(String tourTitle, List<String> songsTitles, String time) {
        boolean result = true;
        if (TourValidator.isValidTourTitle(tourTitle) && !tourTitle.equals(RANDOM) && !tourTitle.equals(TEMPLATE)) {
            if (TourValidator.isValidSongsTitles(songsTitles)) {
                if (TourValidator.isValidTime(time)) {
                    try {
                        TourDao tourDao = new TourDaoImpl();
                        if (tourDao.findIdByTitle(tourTitle) != null) {
                            logger.error("Tour title : " + tourTitle + " has already exist.");
                            result = false;
                        }
                    } catch (DaoException e) {
                        logger.error("Tour dao error due to ", e);
                        result = false;
                    }
                } else {
                    logger.error("Invalid time : " + time);
                    result = false;
                }
            } else {
                logger.error("Invalid songs titles : " + songsTitles);
                result = false;
            }
        } else {
            logger.error("Invalid tour title : " + tourTitle);
            result = false;
        }
        return result;
    }

    private static boolean isValidRequiredSongDetails(String musician, String title, File file) {
        boolean result;
        if (file != null) {
            if (SongValidator.isValidMusician(musician)) {
                if (SongValidator.isValidTitle(title)) {
                    try {
                        SongDao songDao = new SongDaoImpl();
                        result = (songDao.findIdByTitle(musician.concat(DASH).concat(title)) == null);
                    } catch (DaoException e) {
                        logger.error("Song dao error due to ", e);
                        result = false;
                    }
                } else {
                    logger.error("Invalid title : " + title);
                    result = false;
                }
            } else {
                logger.error("Invalid musician : " + musician);
                result = false;
            }
        } else {
            logger.error("File not loaded : " + file);
            result = false;
        }
        return result;
    }

    private static boolean isValidOptionalSongDetails(String genre, String album, String date, String information) {
        boolean result = true;
        if (SongValidator.isValidGenre(genre)) {
            if (SongValidator.isValidAlbum(album)) {
                if (SongValidator.isValidDate(date)) {
                    if (!SongValidator.isValidInformation(information)) {
                        logger.error("Invalid information : " + information);
                        result = false;
                    }
                } else {
                    logger.error("Invalid date : " + date);
                    result = false;
                }
            } else {
                logger.error("Invalid album : " + album);
                result = false;
            }
        } else {
            logger.error("Invalid genre : " + genre);
            result = false;
        }
        return result;

    }

    private static boolean isValidGameDetails(String gameTitle, List<String> toursTitles) {
        boolean result;
        if (GameValidator.isValidGameTitle(gameTitle)) {
            if (GameValidator.isValidToursTitles(toursTitles)) {
                try {
                    GameDao gameDao = new GameDaoImpl();
                    result = (gameDao.findIdByTitle(gameTitle) == null);
                } catch (DaoException e) {
                    logger.error("Game dao error due to ", e);
                    result = false;
                }
            } else {
                logger.error("Invalid tours titles : " + toursTitles);
                result = false;
            }
        } else {
            logger.error("Invalid game title : " + gameTitle);
            result = false;
        }
        return result;
    }

}
