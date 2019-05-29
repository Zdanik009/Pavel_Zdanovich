package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.ArrayList;
import java.util.Map;

import static com.epam.melotrack.service.Service.*;

public class LoadCommonStatisticCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        Map<String, Integer> leaders = LoadLogic.findCommonStatistic();
        requestContent.setRequestAttribute(LEADER + UNDERSCORE + NAMES, new ArrayList<>(leaders.keySet()));
        requestContent.setRequestAttribute(LEADER + UNDERSCORE + RESULTS, new ArrayList<>(leaders.values()));
        return new ResponseContent().setRouter(new Router(COMMON_STATISTIC_ROUT, Router.Type.FORWARD));
    }
}
