package com.epam.melotrack.entity;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class Statistic extends Entity {

    public final static byte DEFAULT_STATISTIC_MAX_RESULT = 100;

    private long userId;
    private Map<Long, Map.Entry<Byte, Timestamp>> statistics = new HashMap<>();//Long - set_of_tours_id, Byte - result, Date - date:)

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Byte getResultBySetOfToursId(Long setOfToursId) {
        return statistics.get(setOfToursId).getKey();
    }

    public Timestamp getDateBySetOfToursId(Long setOfToursId) {
        return statistics.get(setOfToursId).getValue();
    }

    public void put(Long setOfToursId, Byte result) {
        statistics.put(setOfToursId, Map.entry(result, new Timestamp(System.currentTimeMillis())));
    }

    public void put(Long setOfToursId, Byte result, Timestamp date) {
        statistics.put(setOfToursId, Map.entry(result, date));
    }

    public void removeResultBySetOfToursId(Long setOfToursId) {
        statistics.remove(setOfToursId);
    }

    public int size() {
        return statistics.size();
    }

    public List<Long> getSetsOfToursId() {
        return new ArrayList<>(statistics.keySet());
    }

    public Collection<Byte> getResults() {
        return statistics.values().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public Collection<Timestamp> getDates() {
        return statistics.values().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

}
