package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.servlet.RequestContent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static com.epam.melotrack.service.Service.*;

public class ShowMainPageCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        return new ResponseContent().setRouter(new Router(MAIN_ROUT, Router.Type.FORWARD));
    }

}
