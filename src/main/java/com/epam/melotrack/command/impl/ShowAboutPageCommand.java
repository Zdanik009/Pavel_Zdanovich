package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.servlet.RequestContent;

import static com.epam.melotrack.service.Service.ABOUT_ROUT;

public class ShowAboutPageCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        return new ResponseContent().setRouter(new Router(ABOUT_ROUT, Router.Type.FORWARD));
    }

}
