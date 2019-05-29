package com.epam.melotrack.entity;

import com.epam.melotrack.converter.AudioFormat;
import com.epam.melotrack.dao.impl.SongDaoImpl;

import java.io.File;

import static com.epam.melotrack.service.Service.*;

public class Song extends Entity{

    public final static String DEFAULT_SONG_PLAYBACK_INFO = "start_time = 0, enable";
    public final static long DEFAULT_MAX_FILE_SIZE = 16_000_000;

    private long songId;
    private String musician;
    private String title;
    private File file;
    private String playback;
    private short startTime;
    private boolean enable;
    private static AudioFormat audioFormat;
    private String genre;
    private String album;
    private short date;
    private String information;

    public static void setAudioFormat(AudioFormat audioFormat) {
        Song.audioFormat = audioFormat;
    }

    public static AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getMusician() {
        return musician;
    }

    public void setMusician(String musician) {
        this.musician = musician;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public File getFile() {
        return file;
    }

    public String getAudio() {
        return SongDaoImpl.getAudioTemporaryDirectoryPath().getFileName() + SLASH + file.getName();
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPlayback() {
        return playback;
    }

    public void setPlayback(String playback) {
        this.playback = playback;
        this.startTime = Short.valueOf(playback.replaceAll(NON_NUMBER_REGEX, EMPTY_STRING));
        this.enable = playback.contains(ENABLE);
    }

    public short getStartTime() {
        return startTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public short getDate() {
        return date;
    }

    public void setDate(short date) {
        this.date = date;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

}
