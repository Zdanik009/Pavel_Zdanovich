package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.logic.UploadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.List;

import static com.epam.melotrack.service.Service.*;

public class UploadGameCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String gameTitle = (String) requestContent.getRequestParameter(GAME + UNDERSCORE + TITLE);
        List<String> toursTitles = SubmitTourCommand.getContentByName(requestContent, TOUR + UNDERSCORE + TITLE + UNDERSCORE, Game.DEFAULT_GAME_TOURS_AMOUNT);
        Game game;
        if ((game = UploadLogic.uploadGame(gameTitle.trim(), toursTitles)) != null) {
            List<String> allGameTitles = LoadLogic.findAllGameTitles();
            requestContent.setServletContent(GAME + UNDERSCORE + TITLES, allGameTitles);
            requestContent.setRequestAttribute(GAME, game);
            requestContent.setRequestAttribute(UPLOAD + UNDERSCORE + STATUS, UPLOADED);
        } else {
            requestContent.setRequestAttribute(UPLOAD + UNDERSCORE + STATUS, FAILED);
        }
        return new ResponseContent().setRouter(new Router(UPLOAD_GAME_ROUT, Router.Type.FORWARD));
    }
}
