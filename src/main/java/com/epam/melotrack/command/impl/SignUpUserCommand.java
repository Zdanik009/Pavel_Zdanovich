package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.User;
import com.epam.melotrack.logic.Logic;
import com.epam.melotrack.servlet.RequestContent;

import static com.epam.melotrack.service.Service.*;

public class SignUpUserCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String username = (String) requestContent.getRequestParameter(USERNAME);
        String password = (String) requestContent.getRequestParameter(PASSWORD);
        User user;
        if ((user = Logic.register(username, password)) != null) {
            requestContent.setServletContent(USER, user);
            requestContent.setServletContent(ROLE, user.getRole());
            return new ResponseContent().setRouter(new Router(MAIN_ROUT, Router.Type.FORWARD));
        }
        return new ResponseContent().setRouter(new Router(LOGIN_ROUT, Router.Type.FORWARD));
    }

}
