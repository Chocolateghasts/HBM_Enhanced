package com.mewo.hbmenhanced.Util;

import com.mewo.hbmenhanced.ResearchManager.PointManager;

import java.util.*;

public class ResearchValue {
    private final Map<PointManager.ResearchType, Integer> researchPoints;

    public ResearchValue() {
        this.researchPoints = new HashMap<>();
    }
    public ResearchValue(Map<PointManager.ResearchType, Integer> researchPoints) {
        this.researchPoints = new HashMap<>(researchPoints);
    }

    public void addPoints(PointManager.ResearchType type, int points) {
        researchPoints.merge(type, points, Integer::sum);
        System.out.println("Added " + points + " to type " + type.toString());
    }

    public int getPoints(PointManager.ResearchType type) {
        return researchPoints.getOrDefault(type, 0);
    }

    public Map<PointManager.ResearchType, Integer> getAllPoints() {
        return Collections.unmodifiableMap(researchPoints);
    }

    public boolean hasType(PointManager.ResearchType type) {
        return researchPoints.containsKey(type);
    }
}
