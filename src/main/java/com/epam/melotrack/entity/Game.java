package com.epam.melotrack.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Game extends Entity {

    public final static int DEFAULT_GAME_TOURS_AMOUNT = 3;

    private long gameId;
    private String title;
    private List<Tour> tours;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int indexOf(Tour tour) {
        return tours.indexOf(tour);
    }

    public boolean isEmpty() {
        return tours.isEmpty();
    }

    public boolean contains(Tour tour) {
        return tours.contains(tour);
    }

    public List<Tour> getTours() {
        return tours;
    }

    public void setTours(List<Tour> tours) {
    this.tours = tours;
    }

}
