package com.mewo.hbmenhanced.Util;

import com.mewo.hbmenhanced.ResearchManager.PointManager;

public class ResearchValue {
    private final PointManager.ResearchType type;
    private final int points;

    public ResearchValue(PointManager.ResearchType type, int points) {
        this.type = type;
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public PointManager.ResearchType getType() {
        return type;
    }
}
