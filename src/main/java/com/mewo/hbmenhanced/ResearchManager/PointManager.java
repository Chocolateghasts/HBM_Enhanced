package com.mewo.hbmenhanced.ResearchManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mewo.hbmenhanced.Util.Result;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PointManager {

    public enum ResearchType {
        STRUCTURAL, NUCLEAR, SPACE, EXPLOSIVES,
        MACHINERY, WEAPONRY, CHEMICAL, EXOTIC, ELECTRONICS
    }

    private static File dataFile;
    private static final Map<String, EnumMap<ResearchType, Integer>> teamMap = new HashMap<>();

    public static void createFile(World world) {
        dataFile = new File(getDataFolder(world), "teamData.json");

        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                // Optionally, save an empty map
                saveData();
            }
            loadData(); // Ensure it's loaded on creation
        } catch (IOException e) {
            e.printStackTrace();
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

    private static String normalizeTeam(String team) {
        return (team == null) ? null : team.toLowerCase().trim();
    }

    public static List<String> getTeams() {
        return new ArrayList<>(teamMap.keySet());
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

    public static EnumMap<ResearchType, Integer> getAllPoints(String team) {
        EnumMap<ResearchType, Integer> original = teamMap.get(team);
        return original != null ? new EnumMap<>(original) : new EnumMap<>(ResearchType.class);
    }

    public static Result addPoints(String team, ResearchType type, int points, World world) {
        if (dataFile == null || !dataFile.exists()) return new Result(false, "Data file not initialized");
        if (team == null || type == null || points == 0) {
            return new Result(false, "Values are null or zero");
        }

        team = normalizeTeam(team);
        teamMap.putIfAbsent(team, new EnumMap<>(ResearchType.class));
        EnumMap<ResearchType, Integer> map = teamMap.get(team);
        map.put(type, map.getOrDefault(type, 0) + points);
        for (Object obj : world.playerEntities) {
            if (obj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) obj;
                if (points > 0) {
                    player.addChatMessage(new ChatComponentText("Added " + points + " of type " + type.toString() + " to team " + team));
                } else {
                    player.addChatMessage(new ChatComponentText("Removed " + -points + " of type " + type.toString() + " from team " + team));
                }

            }
        }
        return saveData();
    }

    public static Result setPoints(String team, ResearchType type, int points) {
        if (team == null || type == null || points == 0) {
            return new Result(false, "Values are null or zero");
        }
        team = normalizeTeam(team);
        teamMap.putIfAbsent(team, new EnumMap<>(ResearchType.class));
        teamMap.get(team).put(type, points);
        return saveData();
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

    public static Result saveData() {
        if (dataFile == null) {
            return new Result(false, "Data file not initialized");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(dataFile)) {
            // Convert EnumMap<ResearchType, Integer> to Map<String, Integer> for saving
            Map<String, Map<String, Integer>> exportMap = new HashMap<>();
            for (Map.Entry<String, EnumMap<ResearchType, Integer>> entry : teamMap.entrySet()) {
                Map<String, Integer> innerMap = new HashMap<>();
                for (Map.Entry<ResearchType, Integer> inner : entry.getValue().entrySet()) {
                    innerMap.put(inner.getKey().name(), inner.getValue());
                }
                exportMap.put(entry.getKey(), innerMap);
            }
            gson.toJson(exportMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "Failed to write to JSON");
        }

        return new Result(true, "Saved data to JSON");
    }

    public static Result loadData() {
        if (dataFile == null || !dataFile.exists()) {
            return new Result(false, "Data file does not exist");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
            Map<String, Map<String, Integer>> rawMap = gson.fromJson(reader, type);

            if (rawMap == null) {
                return new Result(false, "JSON was empty or invalid");
            }

            teamMap.clear();
            for (Map.Entry<String, Map<String, Integer>> entry : rawMap.entrySet()) {
                EnumMap<ResearchType, Integer> enumMap = new EnumMap<>(ResearchType.class);
                for (Map.Entry<String, Integer> innerEntry : entry.getValue().entrySet()) {
                    try {
                        ResearchType typeEnum = ResearchType.valueOf(innerEntry.getKey());
                        enumMap.put(typeEnum, innerEntry.getValue());
                    } catch (IllegalArgumentException ignored) {
                        // Skip unknown enum values
                    }
                }
                teamMap.put(entry.getKey(), enumMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "Failed to load from JSON");
        }

        return new Result(true, "Loaded from JSON");
    }

    public static Map<String, EnumMap<ResearchType, Integer>> getTeamMap() {
        return teamMap;
    }
}
