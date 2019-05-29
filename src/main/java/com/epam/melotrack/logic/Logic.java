package com.epam.melotrack.logic;

import com.epam.melotrack.dao.GameDao;
import com.epam.melotrack.dao.impl.GameDaoImpl;
import com.epam.melotrack.entity.*;
import com.epam.melotrack.exception.DaoException;
import com.epam.melotrack.service.Scrambler;
import com.epam.melotrack.service.Service;
import com.epam.melotrack.validation.UserValidatior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.melotrack.service.Service.*;

public class Logic {

    private final static Logger logger = LogManager.getLogger();
    private static Map<String, Boolean> answers;

    private Logic() {
        answers = new HashMap<>();
    }

    public static AvaliableLocale changeLanguage(String language) {
        if (language != null && AvaliableLocale.contains(language.toUpperCase())) {
            return AvaliableLocale.valueOf(language.toUpperCase());
        }
        return AvaliableLocale.EN_US;
    }

    public static User login(String username, String password) {
        User user = null;
        if (UserValidatior.isValidUserName(username)) {
            if (UserValidatior.isValidPassword(password)) {
                String passwordHex = Scrambler.encrypt(password);
                user = LoadLogic.findUserByUsername(username);
                if (user != null) {
                    if (!user.getPassword().equals(passwordHex)) {
                        user = null;
                        logger.error("Wrong password for username : " + username);
                    }
                } else {
                    logger.error("Username : " + username + " not registered");
                }
            } else {
                logger.error("Invalid password :" + password);
            }
        } else {
            logger.error("Invalid username : " + username);
        }
        return user;
    }

    public static User register(String username, String password) {
        User user = null;
        if (UserValidatior.isValidUserName(username)) {
            if (UserValidatior.isValidPassword(password)) {
                user = LoadLogic.findUserByUsername(username);
                if (user == null) {
                    user = new User();
                    user.setUserName(username);
                    String passwordHex = Scrambler.encrypt(password);
                    user.setPassword(passwordHex);
                    user.setRole(User.Role.USER.name());
                    user = UploadLogic.uploadUser(user);
                } else {
                    logger.error("Username : " + username + " is already registered");
                    user = null;
                }
            } else {
                logger.error("Invalid password : " + password);
            }
        } else {
            logger.error("Invalid username : " + username);
        }
        return user;
    }

    public static Tour nextTour(Game game, Tour currentTour) {
        Tour nextTour = null;
        if (game != null && game.getTours() != null && !game.isEmpty()) {
            int currentIndex;
            if (currentTour != null && (currentIndex = game.indexOf(currentTour)) != -1) {
                if (currentIndex != game.getTours().size() - 1) {
                    nextTour = game.getTours().get(currentIndex + 1);
                }
            } else {
                nextTour = game.getTours().get(0);
            }
        } else {
            logger.error("Empty game : " + game + " has no tours");
        }
        return prepareTour(nextTour);
    }

    public static Tour prepareTour(Tour tour) {
        if (tour != null && tour.getSongs() != null && tour.getTitle() != null) {
            switch (tour.getTitle()) {
                case Tour.TITLE_COVERS: {
                    List<Song> covers = tour.getSongs().stream().filter(song -> {
                        String songInformation = song.getInformation();
                        return (songInformation != null && song.getInformation().contains(COVER));
                    }).collect(Collectors.toList());
                    List<Song> originals = tour.getSongs();
                    originals.removeAll(covers);
                    List<Song> sortedOriginals = new ArrayList<>();
                    for (Song song : covers) {
                        sortedOriginals.add(originals.stream().filter(s -> s.getTitle().equals(song.getTitle())).findFirst().orElse(song));
                    }
                    covers.addAll(sortedOriginals);
                    tour.setSongs(covers);
                    break;
                }
                case Tour.TITLE_PLAGIARISM: {
                    List<Song> plagiates = tour.getSongs().stream().filter(song -> {
                        String songInformation = song.getInformation();
                        return (songInformation != null && songInformation.contains(PLAGIATE));
                    }).collect(Collectors.toList());
                    List<Song> originals = tour.getSongs();
                    originals.removeAll(plagiates);
                    List<Song> sortedPlagiates = new ArrayList<>();
                    for (Song song : originals) {
                        sortedPlagiates.add(plagiates.stream().filter(s -> {
                            String songInformation = s.getInformation();
                            String songTitle = song.getTitle();
                            return (songInformation != null && songInformation.contains(songTitle));
                        }).findFirst().orElse(song));
                    }
                    originals.addAll(sortedPlagiates);
                    tour.setSongs(originals);
                    break;
                }
                case Tour.TITLE_ROCKPRIVET: {
                    List<Song> songs = tour.getSongs();
                    List<Song> rockprivets = songs.stream().filter(song -> song.getMusician().contains(Tour.TITLE_ROCKPRIVET.toUpperCase())).collect(Collectors.toList());
                    songs.removeAll(rockprivets);
                    List<Song> sortedTextSongs = new ArrayList<>();
                    List<Song> sortedMusicSongs = new ArrayList<>();
                    for (Song song : rockprivets) {
                        sortedTextSongs.add(songs.stream().filter(s -> s.getTitle().contains(song.getTitle())).findFirst().orElse(song));
                        sortedMusicSongs.add(songs.stream().filter(s -> {
                            String songInformation = song.getInformation();
                            return (songInformation != null && songInformation.contains(s.getMusician()));
                        }).findFirst().orElse(song));
                    }
                    rockprivets.addAll(sortedTextSongs);
                    rockprivets.addAll(sortedMusicSongs);
                    tour.setSongs(rockprivets);
                    break;
                }
                default: {
                    logger.info("Tour is already prepared");
                }
            }
        }
        return tour;
    }

    public static byte checkTour(Tour tour, Map<String, List<String>> userAnswers, int categoryAnswersAmount) {
        byte result = 0;
        if (tour != null && tour.getSongs() != null &&!tour.getSongs().isEmpty()) {
            if (userAnswers != null){
                if (categoryAnswersAmount > 0) {
                    switch (tour.getType()) {
                        case Tour.TYPE_SIMPLE: {
                            result = checkSimpleTour(tour, userAnswers, categoryAnswersAmount);
                            break;
                        }
                        case Tour.TYPE_CUSTOM: {
                            result = checkCustomTour(tour, userAnswers, categoryAnswersAmount);
                            break;
                        }
                        default: {
                            result = checkSimpleTour(tour, userAnswers, categoryAnswersAmount);
                        }
                    }
                } else {
                    logger.error("Invalid amount of categories : " + categoryAnswersAmount);
                }
            } else {
                logger.error("Invalid useranswers : " + userAnswers);
            }
        } else {
            logger.error("Invalid tour : " + tour);
        }
        return result;
    }

    private static byte checkSimpleTour(Tour tour, Map<String, List<String>> userAnswers, int answersAmount) {
        List<String> userMusicians = userAnswers.get(MUSICIAN + UNDERSCORE);
        List<String> userTitles = userAnswers.get(TITLE + UNDERSCORE);
        List<String> rightMusicians = new ArrayList<>(answersAmount);
        List<String> rightTitles = new ArrayList<>(answersAmount);
        tour.getSongs().forEach(song -> {
            rightMusicians.add(song.getMusician());
            rightTitles.add(song.getTitle());
        });
        Map<String, Boolean> musicianResults = checkAnswers(rightMusicians, userMusicians);
        Map<String, Boolean> titleResults = checkAnswers(rightTitles, userTitles);
        setAnswers(musicianResults, titleResults);
        return countResult(musicianResults, titleResults);
    }

    private static byte checkCustomTour(Tour tour, Map<String, List<String>> userAnswers, int answersAmount) {
        byte result;
        switch (tour.getTitle()) {
            case Tour.TITLE_CITIES: {
                List<String> userMusicians = userAnswers.get(MUSICIAN + UNDERSCORE);
                List<String> userTitles = userAnswers.get(TITLE + UNDERSCORE);
                List<String> userCities = userAnswers.get(CITY + UNDERSCORE);
                List<String> rightMusicians = new ArrayList<>(answersAmount);
                List<String> rightTitles = new ArrayList<>(answersAmount);
                List<String> rightCities = new ArrayList<>(answersAmount);
                tour.getSongs().forEach(song -> {
                    rightMusicians.add(song.getMusician());
                    rightTitles.add(song.getTitle());
                    rightCities.add(song.getInformation());
                });
                Map<String, Boolean> musicianResults = checkAnswers(rightMusicians, userMusicians);
                Map<String, Boolean> titleResults = checkAnswers(rightTitles, userTitles);
                Map<String, Boolean> citiesResults = checkAnswers(rightCities, userCities);
                result = countResult(musicianResults, titleResults, citiesResults);
                setAnswers(musicianResults, titleResults, citiesResults);
                break;
            }
            case Tour.TITLE_COVERS: {
                List<String> userMusicians = userAnswers.get(ORIGINAL + UNDERSCORE + MUSICIAN + UNDERSCORE);
                List<String> userCoverMusicians = userAnswers.get(COVER + UNDERSCORE + MUSICIAN + UNDERSCORE);
                List<String> userTitles = userAnswers.get(TITLE + UNDERSCORE);
                List<String> rightMusicians = new ArrayList<>(answersAmount);
                Set<String> rightTitles = new LinkedHashSet<>(answersAmount);
                List<String> rightCoverMusicians = new ArrayList<>(answersAmount);
                tour.getSongs().forEach(song -> {
                    String songInformation = song.getInformation();
                    if (songInformation != null && songInformation.contains(COVER)) {
                        rightCoverMusicians.add(song.getMusician());
                    } else {
                        rightMusicians.add(song.getMusician());
                    }
                    rightTitles.add(song.getTitle());
                });
                Map<String, Boolean> musicianResults = checkAnswers(rightMusicians, userMusicians);
                Map<String, Boolean> titleResults = checkAnswers(new ArrayList<>(rightTitles), userTitles);
                Map<String, Boolean> coverMusicianResults = checkAnswers(rightCoverMusicians, userCoverMusicians);
                result = countResult(musicianResults, titleResults, coverMusicianResults);
                setAnswers(musicianResults, titleResults, coverMusicianResults);
                break;
            }
            case Tour.TITLE_PLAGIARISM: {
                List<String> userOriginalMusicians = userAnswers.get(ORIGINAL + UNDERSCORE + MUSICIAN + UNDERSCORE);
                List<String> userOriginalTitles = userAnswers.get(ORIGINAL + UNDERSCORE + TITLE + UNDERSCORE);
                List<String> userPlagiarists = userAnswers.get(PLAGIATE + UNDERSCORE + MUSICIAN + UNDERSCORE);
                List<String> userPlagiateTitles = userAnswers.get(PLAGIATE + UNDERSCORE + TITLE + UNDERSCORE);
                List<String> rightMusicians = new ArrayList<>(answersAmount);
                List<String> rightOriginalTitles = new ArrayList<>(answersAmount);
                List<String> rightPlagiarists = new ArrayList<>(answersAmount);
                List<String> rightPlagiateTitles = new ArrayList<>(answersAmount);
                tour.getSongs().forEach(song -> {
                    String songInformation = song.getInformation();
                    if (songInformation != null && songInformation.contains(PLAGIATE)) {
                        rightPlagiarists.add(song.getMusician());
                        rightPlagiateTitles.add(song.getTitle());
                    } else {
                        rightMusicians.add(song.getMusician());
                        rightOriginalTitles.add(song.getTitle());
                    }
                });
                Map<String, Boolean> originalMusicianResults = checkAnswers(rightMusicians, userOriginalMusicians);
                Map<String, Boolean> originalTitleResults = checkAnswers(rightOriginalTitles, userOriginalTitles);
                Map<String, Boolean> plagiaristResults = checkAnswers(rightPlagiarists, userPlagiarists);
                Map<String, Boolean> plagiateTitleResults = checkAnswers(rightPlagiateTitles, userPlagiateTitles);
                result = countResult(originalMusicianResults, originalTitleResults, plagiaristResults, plagiateTitleResults);
                setAnswers(originalMusicianResults, originalTitleResults, plagiaristResults, plagiateTitleResults);
                break;
            }
            case Tour.TITLE_ROCKPRIVET: {
                List<String> userTextMusicians = userAnswers.get(TEXT + UNDERSCORE + MUSICIAN + UNDERSCORE);
                List<String> userTextTitles = userAnswers.get(TEXT + UNDERSCORE + TITLE + UNDERSCORE);
                List<String> userMusicMusicians = userAnswers.get(MUSIC + UNDERSCORE + MUSICIAN + UNDERSCORE);
                List<String> userMusicTitles = userAnswers.get(MUSIC + UNDERSCORE + TITLE + UNDERSCORE);
                List<String> rightTextMusicians = new ArrayList<>(answersAmount);
                List<String> rightTextTitles = new ArrayList<>(answersAmount);
                List<String> rightMusicMusicians = new ArrayList<>(answersAmount);
                List<String> rightMusicTitles = new ArrayList<>(answersAmount);
                tour.getSongs().forEach(song -> {
                    if (!song.getMusician().equals(Tour.TITLE_ROCKPRIVET.toUpperCase())) {
                        if (song.getInformation() != null && song.getInformation().contains(STYLE_OF)) {
                            rightTextMusicians.add(song.getMusician());
                            rightTextTitles.add(song.getTitle());
                        } else {
                            rightMusicMusicians.add(song.getMusician());
                            rightMusicTitles.add(song.getTitle());
                        }
                    }
                });
                Map<String, Boolean> textMusicianResults = checkAnswers(rightTextMusicians, userTextMusicians);
                Map<String, Boolean> textTitleResults = checkAnswers(rightTextTitles, userTextTitles);
                Map<String, Boolean> musicMusicianResults = checkAnswers(rightTextMusicians, userMusicMusicians);
                Map<String, Boolean> musicTitleResults = checkAnswers(rightTextTitles, userMusicTitles);
                result = countResult(textMusicianResults, textTitleResults, musicMusicianResults, musicTitleResults);
                setAnswers(textMusicianResults, textTitleResults, musicMusicianResults, musicTitleResults);
                break;
            }
            default: {
                logger.error("Unknown custom tour title : " + tour.getTitle());
                result = 0;
            }
        }
        return result;
    }

    private static Map<String, Boolean> checkAnswers(List<String> rightAnswers, List<String> userAnswers) {
        Map<String, Boolean> results = new HashMap<>();
        for (int i = 0; i < rightAnswers.size(); i++) {
            String rightAnswer = rightAnswers.get(i).toLowerCase();
            String userAnswer = userAnswers.get(i).trim().toLowerCase();
            List<String> rightAnswerParts = List.of(rightAnswer.split(MUSIC_WORDS_REGEX));
            boolean result;
            result = rightAnswerParts.stream().allMatch(rightAnswerPart -> userAnswer.contains(rightAnswerPart));
            if (results.containsKey(rightAnswer)) {
                results.put(rightAnswer + i, result);
            } else {
                results.put(rightAnswer, result);
            }
        }
        return results;
    }

    private static byte countResult(Map<String, Boolean>... results) {
        byte points = 0;
        for (int i = 0; i < results.length && points <= Statistic.DEFAULT_STATISTIC_MAX_RESULT; i++) {
            points += results[i].values().stream().filter(Boolean::booleanValue).count();
        }
        return points;
    }

    public static Statistic leavePlayground(User user, Statistic statistic, Game game, Tour tour, String gameStatus, String tourStatus) {
        if (user != null) {
            if (game != null && game.getTours() != null && game.contains(tour)) {
                if (tour != null && tour.getTitle() != null && !tour.getTitle().equals(RANDOM)) {
                    if (gameStatus != null && gameStatus.equals(LOADED)) {
                        int tourIndex;
                        if (tourStatus != null && (tourIndex = game.indexOf(tour)) != -1 && !tourStatus.equals(LOADED)) {
                            List<Tour> tours = game.getTours();
                            for (Tour currentTour : tours.subList(tourIndex, tours.size())) {
                                statistic = recordStatistic(user, statistic, game, currentTour, (byte) 0);
                            }
                        }
                    } else {
                        logger.error("Invalid game status to upload statistic : " + gameStatus);
                    }
                    statistic = UploadLogic.uploadStatistic(statistic);
                } else {
                    logger.error("Invalid tour to record statistic : " + tour);
                }
            } else {
                logger.error("Invalid game to record statistic : " + game);
            }
        } else {
            logger.error("Invalid user leave playground : " + user);
        }
        return statistic;
    }

    public static Statistic recordStatistic(User user, Statistic statistic, Game game, Tour tour, byte result) {
        if (user != null) {
            if (game != null && game.getTours() != null) {
                if (tour != null && game.contains(tour)) {
                    if (statistic == null) {
                        statistic = new Statistic();
                        statistic.setUserId(user.getUserId());
                        logger.info("Create new statistic : " + statistic + " for user : " +  user);
                    }
                    Timestamp date = new Timestamp(System.currentTimeMillis());
                    Long setOfToursId;
                    if ((setOfToursId = findSetOfToursIdByGameIdAndTourId(game.getGameId(), tour.getTourId())) != null) {
                        statistic.put(setOfToursId, result, date);
                        logger.info("Statistic for tour : " + tour.getTitle() + " recorded : " + statistic + ". Result = " + result + "Date = " + date);
                    } else {
                        statistic = null;
                    }
                } else {
                    logger.error("Unable to record statistic for tour : " + tour);
                }
            } else {
                logger.error("Unable to record statistic for game : " + game);
            }
        } else {
            logger.error("Unable to record statistic for user : " + user);
        }
        return statistic;
    }

    private static Long findSetOfToursIdByGameIdAndTourId(Long gameId, Long tourId) {
        Long setOfToursId = null;
        try {
            GameDao gameDao = new GameDaoImpl();
            setOfToursId = gameDao.findSetOfToursIdByGameIdAndTourId(gameId, tourId);
        } catch (DaoException e) {
            logger.error("Game dao error due to ", e);
        }
        return setOfToursId;
    }

    public static Map<String, Boolean> getAnswers() {
        return Logic.answers;
    }

    private static void setAnswers(Map<String, Boolean>... answers) {
        for (Map<String, Boolean> answerMap : answers) {
            Logic.answers.putAll(answerMap);
        }
    }

}
