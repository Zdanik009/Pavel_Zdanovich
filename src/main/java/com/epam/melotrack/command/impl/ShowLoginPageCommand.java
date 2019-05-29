package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.servlet.RequestContent;

import static com.epam.melotrack.service.Service.LOGIN_ROUT;

public class ShowLoginPageCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        return new ResponseContent().setRouter(new Router(LOGIN_ROUT, Router.Type.FORWARD));
    }
}
