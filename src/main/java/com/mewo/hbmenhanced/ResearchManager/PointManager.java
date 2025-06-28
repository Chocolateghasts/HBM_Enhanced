package com.mewo.hbmenhanced.ResearchManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mewo.hbmenhanced.Util.Result;
import net.minecraft.world.World;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PointManager {

    public enum ResearchType {STRUCTURAL, NUCLEAR, SPACE, EXPLOSIVES, MACHINERY, WEAPONRY, CHEMICAL, EXOTIC, ELECTRONICS}

    static File dataFile;

    public static void createFile(World world) {
        dataFile = new File(getDataFolder(world), "teamData.json");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getDataFolder(World world) {
        File worldFolder = world.getSaveHandler().getWorldDirectory();
        File myDataFolder = new File(worldFolder, "hbmenhanced");
        if (!myDataFolder.exists()) {
            myDataFolder.mkdirs();
        }
        return myDataFolder;
    }

    public static Map<String, EnumMap<ResearchType, Integer>> teamMap = new HashMap<>();

    public static List<String> getTeams() {
        return new ArrayList<>(teamMap.keySet());
    }

    private static String normalizeTeam(String team) {
        return (team == null) ? null : team.toLowerCase().trim();
    }

    public static Result addTeam(String team) {
        if (team == null) return new Result(false, "Team is null");
        team = normalizeTeam(team);
        if (teamMap.containsKey(team)) {
            return new Result(false, "Team " + team + " is already registered");
        }
        teamMap.put(team, new EnumMap<>(ResearchType.class));
        return new Result(true, "Registered " + team);
    }

    public static Result removeTeam(String team) {
        if (team == null) return new Result(false, "Team is null");
        team = normalizeTeam(team);
        if (!teamMap.containsKey(team)) {
            return new Result(false, "Team is not registered");
        }
        teamMap.remove(team);
        return new Result(true, "Removed " + team);
    }

    public static Result addPoints(String team, ResearchType type, int points) {
        loadData();
        if (team == null || type == null || points == 0) {
            return new Result(false, "Values are null or zero");
        }
        team = normalizeTeam(team);
        teamMap.putIfAbsent(team, new EnumMap<>(ResearchType.class));
        EnumMap<ResearchType, Integer> map = teamMap.get(team);
        map.put(type, map.getOrDefault(type, 0) + points);
        saveData();
        return new Result(true, "Added " + points + " points to " + type + " for team " + team);
    }

    public static Result saveData() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(teamMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "Failed to write to Json");
        }
        return new Result(true, "Saved data to Json");
    }

    public static Result loadData() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, EnumMap<ResearchType, Integer>>>(){}.getType();
            teamMap = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "Failed to load from Json");
        }
        return new Result(true, "Loaded from Json");
    }

    public static Map<String, EnumMap<ResearchType, Integer>> getTeamMap() {
        return teamMap;
    }

    public static int getPoints(String team, ResearchType type) {
        if (team == null || type == null) {
            return 0;
        }
        team = normalizeTeam(team);
        if (!teamMap.containsKey(team)) {
            return 0;
        }
        return teamMap.get(team).getOrDefault(type, 0);
    }

    public static Result setPoints(String team, ResearchType type, int points) {
        if (team == null || type == null || points == 0) {
            return new Result(false, "Values are null or zero");
        }
        team = normalizeTeam(team);
        if (!teamMap.containsKey(team)) {
            addTeam(team);
        }
        teamMap.get(team).put(type, points);
        return new Result(true, "Set points of type " + type + " to " + points + " of team " + team);
    }
}
