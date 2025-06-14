package com.mewo.hbmenhanced.ResearchValuesManager;

import com.mewo.hbmenhanced.Util.Result;

import java.util.*;

public class PointManager {

    public enum ResearchType {STRUCTURAL, NUCLEAR, SPACE, EXPLOSIVES, MACHINERY, WEAPONRY, CHEMICAL, EXOTIC, ELECTRONICS}

    public Map<String, EnumMap<ResearchType, Integer>> teamMap = new HashMap<>();

    public List<String> getTeams() {
        return new ArrayList<>(teamMap.keySet());
    }

    public Result addTeam(String team) {
        if (team == null) return new Result(false, "Team is null");
        team = team.toLowerCase().trim();
        if (teamMap.containsKey(team)) {
            return new Result(false, "Team " + team + " is already registered");
        }
        teamMap.put(team, new EnumMap<>(ResearchType.class));
        return new Result(true, "Registered " + team);
    }

    public Result removeTeam(String team) {
        if (team == null) return new Result(false, "Team is null");
        team = team.toLowerCase().trim();
        if (!teamMap.containsKey(team)) {
            return new Result(false, "Team is not registered");
        }
        teamMap.remove(team);
        return new Result(true, "Removed " + team);
    }

    public Result addPoints(String team, ResearchType type, int points) {
        team = team.toLowerCase().trim();
        if (team == null || type == null || points == 0) {
            return new Result(false, "Values are null");
        }
        if (!teamMap.containsKey(team)) {
            addTeam(team);
        }
        return new Result(true, "");

    }
}
