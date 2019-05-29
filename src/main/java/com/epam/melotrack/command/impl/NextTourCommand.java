package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.logic.Logic;
import com.epam.melotrack.logic.UploadLogic;
import com.epam.melotrack.servlet.RequestContent;

import static com.epam.melotrack.service.Service.*;

public class NextTourCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        Game game = (Game) requestContent.getServletContent(GAME);
        Tour tour = (Tour) requestContent.getServletContent(TOUR);
        Statistic statistic = (Statistic) requestContent.getServletContent(USER + UNDERSCORE + STATISTIC);
        if ((tour = Logic.nextTour(game, tour)) != null) {
            requestContent.setServletContent(TOUR, tour);
            requestContent.setServletContent(TOUR + UNDERSCORE + STATUS, LOADED);
        } else {
            if ((statistic = UploadLogic.uploadStatistic(statistic)) != null) {
                requestContent.setServletContent(USER + UNDERSCORE + STATISTIC, statistic);
            }
            requestContent.setServletContent(GAME + UNDERSCORE + STATUS, FINISHED);
        }
        return new ResponseContent().setRouter(new Router(PLAYGROUND_ROUT, Router.Type.FORWARD));
    }
}
