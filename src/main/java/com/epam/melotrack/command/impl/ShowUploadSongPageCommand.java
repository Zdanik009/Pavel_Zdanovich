package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.logic.LoadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.util.List;
import java.util.stream.Collectors;

import static com.epam.melotrack.service.Service.*;

public class ShowUploadSongPageCommand implements Command {


    @Override
    public ResponseContent execute(RequestContent requestContent) {
        return new ResponseContent().setRouter(new Router(UPLOAD_SONG_ROUT, Router.Type.FORWARD));
    }
}
