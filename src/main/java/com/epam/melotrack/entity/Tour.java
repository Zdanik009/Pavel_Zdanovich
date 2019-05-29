package com.epam.melotrack.entity;

import com.epam.melotrack.dao.impl.TourDaoImpl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.melotrack.service.Service.*;

public class Tour extends Entity {

    public final static String TITLE_RANDOM = "random";
    public final static String TITLE_BELARUSSIAN = "belarussian";
    public final static String TITLE_CITIES = "cities";
    public final static String TITLE_WHISTLING = "whistling";
    public final static String TITLE_SOLO = "solo";
    public final static String TITLE_COVERS = "covers";
    public final static String TITLE_PLAGIARISM = "plagiarism";
    public final static String TITLE_ROCKPRIVET = "rockprivet";
    public final static String TITLE_TEMPLATE = "template";
    public final static String TYPE_SIMPLE = "simple";
    public final static String TYPE_CUSTOM = "custom";
    public final static int DEFAULT_TOUR_SONGS_AMOUNT = 10;
    public final static long RANDOM_TOUR_ID = 8;
    public final static long TEMPLATE_TOUR_ID = 9;

    private long tourId;
    private String title;
    private List<Song> songs;
    private File rules;
    private byte time;
    private String type;
    private List<String> categories;

    public long getTourId() {
        return tourId;
    }

    public void setTourId(long tourId) {
        this.tourId = tourId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public static int getDefaultTourSongsAmount() {
        return DEFAULT_TOUR_SONGS_AMOUNT;
    }

    public File getRules() {
        return rules;
    }

    public void setRules(File rules) {
        this.rules = rules;
    }

    public byte getTime() {
        return time;
    }

    public void setTime(byte time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getBlank() {
        return TourDaoImpl.getBlankTemporaryDirectory().getFileName() + SLASH + rules.getName();
    }

}
