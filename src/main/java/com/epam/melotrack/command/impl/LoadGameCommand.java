package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;

import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.logic.Logic;
import com.epam.melotrack.servlet.RequestContent;


import static com.epam.melotrack.service.Service.*;

public class LoadGameCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String gameTitle = (String) requestContent.getRequestParameter(GAME + UNDERSCORE + TITLE);
        Game game = LoadLogic.findGameByTitle(gameTitle);
        Tour tour = Logic.nextTour(game, null);
        requestContent.setServletContent(GAME, game);
        requestContent.setServletContent(TOUR, tour);
        requestContent.setServletContent(GAME + UNDERSCORE + STATUS, LOADED);
        requestContent.setServletContent(TOUR + UNDERSCORE + STATUS, LOADED);
        return new ResponseContent().setRouter(new Router(PLAYGROUND_ROUT, Router.Type.FORWARD));
    }

}
