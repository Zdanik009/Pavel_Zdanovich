package com.epam.melotrack.command.impl;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.ResponseContent;
import com.epam.melotrack.command.Router;
import com.epam.melotrack.entity.Song;
import com.epam.melotrack.logic.UploadLogic;
import com.epam.melotrack.servlet.RequestContent;

import java.io.File;

import static com.epam.melotrack.service.Service.*;

public class UploadSongCommand implements Command {

    @Override
    public ResponseContent execute(RequestContent requestContent) {
        String musician = (String) requestContent.getRequestParameter(MUSICIAN);
        String title = (String) requestContent.getRequestParameter(TITLE);
        String genre = (String) requestContent.getRequestParameter(GENRE);
        String album = (String) requestContent.getRequestParameter(ALBUM);
        String date = (String) requestContent.getRequestParameter(DATE);
        String information = (String) requestContent.getRequestParameter(INFORMATION);
        File file = requestContent.getRequestPart(FILE);
        Song song;
        if ((song = UploadLogic.uploadSong(musician.trim(), title.trim(), genre.trim(), album.trim(), date.trim(), information.trim(), file)) != null) {
            requestContent.setRequestAttribute(SONG, song);
            requestContent.setRequestAttribute(UPLOAD + UNDERSCORE + STATUS, UPLOADED);
        } else {
            requestContent.setRequestAttribute(UPLOAD + UNDERSCORE + STATUS, FAILED);
        }
        return new ResponseContent().setRouter(new Router(UPLOAD_SONG_ROUT, Router.Type.FORWARD));
    }
}
