package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.logic.UploadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.List;

import static com.epam.melotrack.service.Service.*;

public class UploadTourCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String tourTitle = (String) requestContent.getRequestParameter(TOUR + UNDERSCORE + TITLE);
        List<String> songsTitles = SubmitTourCommand.getContentByName(requestContent, SONG + UNDERSCORE + TITLE + UNDERSCORE, Tour.DEFAULT_TOUR_SONGS_AMOUNT);
        String time = (String) requestContent.getRequestParameter(TIME);
        Tour tour;
        if ((tour = UploadLogic.uploadTour(tourTitle.trim(), songsTitles, time.trim())) != null) {
            requestContent.setRequestAttribute(TOUR, tour);
            requestContent.setRequestAttribute(UPLOAD + UNDERSCORE + STATUS, UPLOADED);
        } else {
            requestContent.setRequestAttribute(UPLOAD + UNDERSCORE + STATUS, FAILED);

        }
        return new ResponseContent().setRouter(new Router(UPLOAD_TOUR_ROUT, Router.Type.FORWARD));
    }

}
