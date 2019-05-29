package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.entity.User;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.List;

import static com.epam.melotrack.service.Service.*;

public class LoadUserStatisticCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        User user = (User) requestContent.getServletContent(USER);
        Statistic statistic = LoadLogic.findUserStatistic(user);
        List<String> gameToursTitles = LoadLogic.findGameAndToursTitlesOfStatistic(statistic);
        if (statistic != null) {
            requestContent.setServletContent(USER + UNDERSCORE + STATISTIC, statistic);
            requestContent.setServletContent(GAME + UNDERSCORE + TOURS + UNDERSCORE + TITLES, gameToursTitles);
        }
        return new ResponseContent().setRouter(new Router(USER_STATISTIC_ROUT, Router.Type.FORWARD));
    }

}
