package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.entity.User;
import com.epam.melotrack.logic.Logic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.*;

import static com.epam.melotrack.service.Service.*;

public class SubmitTourCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        Game game = (Game) requestContent.getServletContent(GAME);
        Tour tour = (Tour) requestContent.getServletContent(TOUR);
        Map<String, List<String>> userAnswers = new HashMap<>();
        List<String> tourCategories = tour.getCategories();
        tourCategories.forEach(category -> userAnswers.put(category + UNDERSCORE, getContentByName(requestContent, category + UNDERSCORE, Tour.DEFAULT_TOUR_SONGS_AMOUNT)));
        byte result = Logic.checkTour(tour, userAnswers, Tour.DEFAULT_TOUR_SONGS_AMOUNT);
        Map<String, Boolean> answers = Logic.getAnswers();
        User user = (User) requestContent.getServletContent(USER);
        Statistic statistic = (Statistic) requestContent.getServletContent(USER + UNDERSCORE + STATISTIC);
        if ((statistic = Logic.recordStatistic(user, statistic, game, tour, result)) != null) {
            requestContent.setServletContent(USER + UNDERSCORE + STATISTIC, statistic);
        }
        if (Logic.nextTour(game, tour) == null) {
            requestContent.setServletContent(GAME + UNDERSCORE + STATUS, FINISHED);
        }
        requestContent.setRequestAttribute(RESULT, result);
        requestContent.setServletContent(TOUR + UNDERSCORE + STATUS, SUBMITTED);
        requestContent.setRequestAttribute(ANSWERS, answers);
        return new ResponseContent().setRouter(new Router(PLAYGROUND_ROUT, Router.Type.FORWARD));
    }

    public static List<String> getContentByName(RequestContent requestContent, String name, int contentAmount) {
        List<String> answers = new ArrayList<>();
        for (int i = 0; i < contentAmount; i++) {
            answers.add(((String) requestContent.getRequestParameter(name + i)).trim());
        }
        return answers;
    }

}
