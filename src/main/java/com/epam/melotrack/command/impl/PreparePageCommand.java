package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.*;

import static com.epam.melotrack.service.Service.*;

public class PreparePageCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String language = (String) requestContent.getRequestParameter(LANGUAGE);
        requestContent.setServletContent(LANGUAGE, language);
        List<String> gameTitles = LoadLogic.findAllGameTitles();
        requestContent.setServletContent(GAME + UNDERSCORE + TITLES, gameTitles);
        return new ResponseContent().setRouter(new Router(MAIN_ROUT, Router.Type.FORWARD));
    }
}
