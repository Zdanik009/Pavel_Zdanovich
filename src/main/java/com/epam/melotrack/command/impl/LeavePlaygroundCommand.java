package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.entity.User;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.logic.Logic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.List;

import static com.epam.melotrack.service.Service.*;
import static com.epam.melotrack.service.Service.FINISHED;

public class LeavePlaygroundCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        User user = (User) requestContent.getServletContent(USER);
        Game game = (Game) requestContent.getServletContent(GAME);
        Tour tour = (Tour) requestContent.getServletContent(TOUR);
        String gameStatus = (String) requestContent.getServletContent(GAME + UNDERSCORE + STATUS);
        String tourStatus = (String) requestContent.getServletContent(TOUR + UNDERSCORE + STATUS);
        Statistic statistic = (Statistic) requestContent.getServletContent(USER + UNDERSCORE + STATISTIC);
        statistic = Logic.leavePlayground(user, statistic, game, tour, gameStatus, tourStatus);
        List<String> gameTitles = LoadLogic.findAllGameTitlesForUser(user, statistic);
        requestContent.setServletContent(USER + UNDERSCORE + STATISTIC, statistic);
        requestContent.setServletContent(GAME + UNDERSCORE + TITLES, gameTitles);
        requestContent.setServletContent(TOUR + UNDERSCORE + STATUS, FINISHED);
        requestContent.setServletContent(GAME + UNDERSCORE + STATUS, FINISHED);
        String rout = (String) requestContent.getRequestParameter(ROUT);
        return new ResponseContent().setRouter(new Router(rout, Router.Type.FORWARD));
    }
}
