package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.servlet.RequestContent;

import static com.epam.melotrack.service.Service.*;

public class StartTourCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        requestContent.setServletContent(TOUR + UNDERSCORE + STATUS, STARTED);
        return new ResponseContent().setRouter(new Router(PLAYGROUND_ROUT, Router.Type.FORWARD));
    }
}
