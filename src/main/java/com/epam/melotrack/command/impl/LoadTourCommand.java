package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.servlet.RequestContent;

import static com.epam.melotrack.service.Service.*;

public class LoadTourCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String tourTitle = (String) requestContent.getRequestParameter(TOUR  + UNDERSCORE + TITLE);
        Tour tour = LoadLogic.findTourByTitle(tourTitle);
        requestContent.setServletContent(TOUR, tour);
        requestContent.setServletContent(TOUR + UNDERSCORE + STATUS, LOADED);
        return new ResponseContent().setRouter(new Router(PLAYGROUND_ROUT, Router.Type.FORWARD));
    }

}

