package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.logic.AvaliableLocale;
import com.epam.melotrack.logic.Logic;
import com.epam.melotrack.servlet.RequestContent;

import static com.epam.melotrack.service.Service.*;

public class ChangeLanguageCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String language = (String) requestContent.getRequestParameter(LANGUAGE);
        AvaliableLocale avaliableLocale = Logic.changeLanguage(language);
        requestContent.setServletContent(LANGUAGE, avaliableLocale.toString());
        return new ResponseContent().setRouter(new Router(MAIN_ROUT, Router.Type.FORWARD));
    }
}
