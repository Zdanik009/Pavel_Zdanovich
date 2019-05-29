package com.epam.melotrack.logic;

import com.epam.melotrack.dao.*;
import com.epam.melotrack.dao.impl.*;
import com.epam.melotrack.entity.*;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.validation.GameValidator;
import com.epam.melotrack.validation.TourValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.melotrack.service.Service.*;

public class LoadLogic {

    private final static Logger logger = LogManager.getLogger();

    private LoadLogic() {
    }

    public static Statistic findUserStatistic(User user) {
        Statistic statistic = null;
        try {
            if (user != null && user.getUserName() != null && user.getPassword() != null && user.getRole() != null) {
                StatisticDao statisticDao = new StatisticDaoImpl();
                statistic = statisticDao.findByUserId(user.getUserId());
            } else {
                logger.error("Invalid user : " + user);
            }
        } catch (DaoException e) {
            logger.error("Error in statistic dao due to ", e);
        }
        return statistic;
    }

    public static Map<String, Integer> findCommonStatistic() {
        Map<String, Integer> leaders = new HashMap<>();
        try {
            UserDao userDao = new UserDaoImpl();
            StatisticDao statisticDao = new StatisticDaoImpl();
            List<User> users = userDao.findAll();
            for (User user : users) {
                Statistic statistic = statisticDao.findByUserId(user.getUserId());
                if (statistic != null) {
                    leaders.put(user.getUserName(), statistic.getResults().stream().mapToInt(Byte::intValue).sum());
                }
            }
        } catch (DaoException e) {
            logger.error("Error in user or statistic dao due to ", e);
        }
        return leaders.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public static Game findGameByTitle(String gameTitle) {
        Game game = null;
        if (GameValidator.isValidGameTitle(gameTitle)) {
            try {
                GameDaoImpl gameDao = new GameDaoImpl();
                game = gameDao.findByTitle(gameTitle);
            } catch (DaoException e) {
                logger.error("Error in game dao due to ", e);
            }
        } else {
            logger.error("Invalid game title : " + gameTitle);
        }
        return game;
    }

    public static Tour findTourByTitle(String tourTitle) {
        Tour tour = null;
        if (TourValidator.isValidTourTitle(tourTitle)) {
            try {
                TourDaoImpl tourDao = new TourDaoImpl();
                tour = tourDao.findByTitle(tourTitle);
            } catch (DaoException e) {
                logger.error("Error in tour dao due to ", e);
            }
        } else {
            logger.error("Invalid tour title : " + tourTitle);
        }
        return tour;
    }

    public static List<String> findAllSongTitles() {
        List<String> songTitles = null;
        try {
            SongDao songDao = new SongDaoImpl();
            songTitles = songDao.findAllTitles();
        } catch (DaoException e) {
            logger.error("Error in tour dao due to ", e);
        }
        return songTitles;
    }

    public static List<Long> findSongsIdByTitles(List<String> songsTitles) {
        List<Long> songsId = null;
        if (songsTitles != null && !songsTitles.isEmpty()) {
            songsId = new ArrayList<>();
            try {
                SongDao songDao = new SongDaoImpl();
                for (String songTitle: songsTitles){
                    songsId.add(songDao.findIdByTitle(songTitle));
                }
            } catch (DaoException e) {
                logger.error("Error in song dao due to ", e);
            }
        }
        return songsId;
    }

    public static List<String> findGameAndToursTitlesOfStatistic(Statistic statistic) {
        List<String> gameToursTitles = null;
        if (statistic != null && statistic.getSetsOfToursId() != null && !statistic.getSetsOfToursId().isEmpty()) {
            List<String> gameTitles = LoadLogic.findGameTitlesBySetsOfToursId(statistic.getSetsOfToursId());
            List<String> tourTitles = LoadLogic.findTourTitlesBySetsOfToursId(statistic.getSetsOfToursId());
            gameToursTitles = new ArrayList<>(gameTitles.size());
            for (int i = 0; i < gameTitles.size(); i++) {
                gameToursTitles.add(tourTitles.get(i).concat(OPEN_ROUND_BRACKET).concat(gameTitles.get(i)).concat(CLOSE_ROUND_BRACKET));
            }
        }
        return gameToursTitles;
    }

    public static List<String> findAllTourTitles() {
        List<String> tourTitles = null;
        try {
            TourDao tourDao = new TourDaoImpl();
            tourTitles = tourDao.findAllTitles();
        } catch (DaoException e) {
            logger.error("Error in tour dao due to ", e);
        }
        return tourTitles;
    }

    public static File findTemplateTourRules() {
        File file = null;
        try {
            TourDao tourDao = new TourDaoImpl();
            file = tourDao.findTemplateTourRules();
        } catch (DaoException e) {
            logger.error("Error in tour dao due to ", e);
        }
        return file;
    }

    public static Long findTourIdByTitle(String tourTitle) {
        Long tourId = null;
        if (tourTitle != null) {
            try {
                TourDao tourDao = new TourDaoImpl();
                tourId = tourDao.findIdByTitle(tourTitle);
            } catch (DaoException e) {
                logger.error("Error in tour dao due to ", e);
            }
        }
        return tourId;
    }

    public static List<Long> findToursIdByTitles(List<String> tourTitles) {
        List<Long> toursId = null;
        if (tourTitles != null && !tourTitles.isEmpty() && tourTitles.stream().noneMatch(title -> title == null)) {
            toursId = new ArrayList<>();
            try {
                TourDao tourDao = new TourDaoImpl();
                for (String tourTitle : tourTitles) {
                    toursId.add(tourDao.findIdByTitle(tourTitle));
                }
            } catch (DaoException e) {
                logger.error("Error in song dao due to ", e);
            }
        }
        return toursId;
    }

    public static List<String> findAllGameTitles() {
        List<String> gameTitles = null;
        try {
            GameDao gameDao = new GameDaoImpl();
            gameTitles = gameDao.findAllTitles();
        } catch (DaoException e) {
            logger.error("Error in game dao due to ", e);
        }
        return gameTitles;
    }

    public static List<String> findAllGameTitlesForUser(User user, Statistic statistic) {
        List<String> gameTitles = null;
        if (user != null && user.getRole() != null && user.getRole().equals(USER)) {
            if (statistic != null && statistic.getSetsOfToursId() != null) {
                List<Long> setsOfToursId = statistic.getSetsOfToursId();
                List<String> allGameTitles = findAllGameTitles();
                List<String> finishedGameTitles = findGameTitlesBySetsOfToursId(setsOfToursId);
                allGameTitles.removeAll(finishedGameTitles);
                gameTitles = allGameTitles;
            } else {
                logger.error("User : " + user + " has no statistic : " + statistic);
                gameTitles = findAllGameTitles();
            }
        } else {
            logger.error("Invalid user : " + user);
            //gameTitles = findAllGameTitles();
        }
        return gameTitles;
    }

    public static List<Long> findGamesIdByTitles(List<String> gameTitles) {
        List<Long> gamesId = null;
        if (gameTitles != null && !gameTitles.isEmpty() && gameTitles.stream().noneMatch(title -> title == null)) {
            gamesId = new ArrayList<>();
            try {
                GameDao gameDao = new GameDaoImpl();
                for (String gameTitle : gameTitles) {
                    gamesId.add(gameDao.findIdByTitle(gameTitle));
                }
            } catch (DaoException e) {
                logger.error("Error in tour dao due to ", e);
            }
        } else {
            logger.error("Invalid game titles : " + gameTitles);
        }
        return gamesId;
    }

    public static User findUserByUsername(String username) {
        User user = null;
        if (username != null) {
            try {
                UserDaoImpl userDao = new UserDaoImpl();
                user = userDao.findByUsername(username);
            } catch (DaoException e) {
                logger.error("Error in user dao due to ", e);
            }
        }
        return user;
    }

    private static List<Song> findSongsByTitles(List<String> songTitles) {
        List<Song> songs = null;
        if (songTitles != null && !songTitles.isEmpty()) {
            songs = new ArrayList<>();
            try {
                for (String songTitle : songTitles) {
                    SongDao songDao = new SongDaoImpl();
                    songs.add(songDao.findByTitle(songTitle));
                }
            } catch (DaoException e) {
                logger.error("Error in song dao due to ", e);
            }
        }
        return songs;
    }

    private static List<String> findTourTitlesBySetsOfToursId(List<Long> setsOfToursId) {
        List<String> tourTitles = null;
        if (setsOfToursId != null && !setsOfToursId.isEmpty()) {
            tourTitles = new ArrayList<>();
            try {
                TourDao tourDao = new TourDaoImpl();
                for (Long setOfTourId : setsOfToursId) {
                    tourTitles.add(tourDao.findTitleBySetOfToursId(setOfTourId));
                }
            } catch (DaoException e) {
                logger.error("Tour dao error due to ", e);
            }
        }
        return tourTitles;
    }

    private static List<String> findToursTitleById(List<Long> toursId) {
        List<String> toursTitle = null;
        if (toursId != null && !toursId.isEmpty()) {
            toursTitle = new ArrayList<>();
            try {
                TourDao tourDao = new TourDaoImpl();
                for (Long tourId : toursId) {
                    toursTitle.add(tourDao.findTitleById(tourId));
                }
            } catch (DaoException e) {
                logger.error("Tour dao error due to ", e);
            }
        }
        return toursTitle;
    }

    private static List<String> findGameTitlesById(List<Long> gamesId) {
        List<String> gameTitles = null;
        if (gamesId != null && !gamesId.isEmpty()) {
            gameTitles = new ArrayList<>();
            try {
                GameDao gameDao = new GameDaoImpl();
                for (Long gameId: gamesId){
                    gameTitles.add(gameDao.findTitleById(gameId));
                }
            } catch (DaoException e) {
                logger.error("Game dao error due to ", e);
            }
        }
        return gameTitles;
    }

    private static List<String> findGameTitlesBySetsOfToursId(List<Long> setsOfToursId) {
        List<String> gamesId = null;
        if (setsOfToursId != null && !setsOfToursId.isEmpty()) {
            gamesId = new ArrayList<>();
            try {
                GameDao gameDao = new GameDaoImpl();
                for (Long setOfToursId : setsOfToursId) {
                    gamesId.add(gameDao.findTitleBySetOfToursId(setOfToursId));
                }
            } catch (DaoException e) {
                logger.error("Game dao error due to ", e);
            }
        }
        return gamesId;
    }

}
