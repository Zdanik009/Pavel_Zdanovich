package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.List;

import static com.epam.melotrack.service.Service.*;

public class ShowUploadGamePageCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        List<String> toursTitles = LoadLogic.findAllTourTitles();
        requestContent.setServletContent(TOURS + UNDERSCORE + TITLES, toursTitles);
        return new ResponseContent().setRouter(new Router(UPLOAD_GAME_ROUT, Router.Type.FORWARD));
    }

}
