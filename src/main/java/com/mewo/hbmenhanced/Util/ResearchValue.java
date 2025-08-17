package com.mewo.hbmenhanced.Util;

import com.hbm.util.Tuple;
import com.mewo.hbmenhanced.ResearchManager.PointManager;

import java.util.*;

public class ResearchValue {
    private final Map<PointManager.ResearchType, Integer> researchPoints;
    private final int researchTime;

    public ResearchValue() {
        this.researchTime = 0;
        this.researchPoints = new HashMap<>();
    }
    public ResearchValue(Map<PointManager.ResearchType, Integer> researchPoints, int researchTime) {
        this.researchPoints = new HashMap<>(researchPoints);
        this.researchTime = researchTime;
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

    public static Tuple.Pair<PointManager.ResearchType, Integer> pairOf(PointManager.ResearchType type, int p) {
        return new Tuple.Pair<>(type, p);
    }

    public void multiply(double factor) {
        for (Map.Entry<PointManager.ResearchType, Integer> entry : researchPoints.entrySet()) {
            int newValue = (int) Math.round(entry.getValue() * factor);
            entry.setValue(newValue);
        }
    }

    @SafeVarargs
    public static Map<PointManager.ResearchType, Integer> mapOf(Tuple.Pair<PointManager.ResearchType, Integer>... pairs) {
        Map<PointManager.ResearchType, Integer> map = new HashMap<>();
        for (Tuple.Pair<PointManager.ResearchType, Integer> pair : pairs) {
            map.put(pair.key, pair.value);
        }
        return map;
    }

    public ResearchValue copy() {
        return new ResearchValue(new HashMap<>(this.researchPoints), this.researchTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchValue)) return false;
        ResearchValue that = (ResearchValue) o;
        return researchTime == that.researchTime &&
                researchPoints.equals(that.researchPoints);
    }

    @Override
    public int hashCode() {
        int result = researchPoints.hashCode();
        result = 31 * result + researchTime;
        return result;
    }

    public int getResearchTime() {
        return researchTime;
    }
}
