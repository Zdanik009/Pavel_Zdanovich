package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.List;

import static com.epam.melotrack.service.Service.*;

public class ShowUploadTourPageCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        List<String> songsTitles = LoadLogic.findAllSongTitles();
        requestContent.setServletContent(SONGS + UNDERSCORE + TITLES, songsTitles);
        return new ResponseContent().setRouter(new Router(UPLOAD_TOUR_ROUT, Router.Type.FORWARD));
    }

}
