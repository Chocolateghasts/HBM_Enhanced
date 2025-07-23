package com.mewo.hbmenhanced.OpenComputers.old;


import com.mewo.hbmenhanced.getRpValue;

import java.util.*;

public class ResearchNodeold {
    protected String name;
    protected String id;
    protected String category;
    protected String description;
    protected int level;
    protected Boolean unlocked;
    protected List<String> dependencies;
    protected List<Map<String, Object>> templates;
    protected HashMap<getRpValue.researchType, Integer> requirements;
//    protected int templateId;
//    protected String templateType;
    protected Map<String, Boolean> teamUnlocked;
    protected float xPos;
    protected float yPos;
    protected getRpValue.researchType type;

    public ResearchNodeold() {
        this.dependencies = new ArrayList<>();
        this.requirements = new HashMap<>();
        this.unlocked = false;
        this.level = 0;
        this.teamUnlocked = new HashMap<>();
        this.templates = new ArrayList<>();
    }


    public void changeDependency(String id, boolean add) {
        if (add) {
            dependencies.add(id);
        } else if (!add) {
            dependencies.remove(id);
        }
    }

    public void changeRequirement(getRpValue.researchType type, int points, boolean add) {
        if (add) {
            requirements.put(type, points);
        } else if (!add) {
            requirements.remove(type);
        }
    }
    public Boolean getUnlocked(String ownerId) {
        return teamUnlocked.getOrDefault(ownerId, false);  // Default to false if the owner is not in the map
    }
    public void unlock(String ownerId) {
        if (!teamUnlocked.containsKey(ownerId)) {
            teamUnlocked.put(ownerId, true);
        }
    }
}
