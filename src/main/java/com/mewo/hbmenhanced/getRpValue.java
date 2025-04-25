package com.mewo.hbmenhanced;

import li.cil.repack.org.luaj.vm2.ast.Str;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.storage.ISaveHandler;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import net.minecraft.world.storage.SaveHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class getRpValue {
    private static final Log log = LogFactory.getLog(getRpValue.class);
    private static HashMap<String, EnumMap<researchType, Integer>> rpValues = new HashMap<>();
    private static HashMap<String, EnumMap<researchType, Integer>> teamRpValues = new HashMap<>();
    public static HashMap<String, EnumMap<researchType, Integer>> getRpMap() {
        return rpValues;
    }
    public static MinecraftServer minecraftServer;
    public static HashMap<String, EnumMap<researchType, Integer>> getTeamRpMap() {
        return teamRpValues;
    }

    public enum researchType {
        STRUCTURAL, NUCLEAR, SPACE, EXPLOSIVES, MACHINERY, WEAPONRY, CHEMICAL, EXOTIC, ELECTRONICS
    }

    public void loadHashMap() {
        for (Object obj : Item.itemRegistry) {
            Item item = (Item) obj;
            if (isBlacklisted(item)) continue;

            String itemName = StatCollector.translateToLocal(item.getUnlocalizedName() + ".name").toLowerCase();
            EnumMap<researchType, Integer> researchPoints = getRpValuesForItem(new ItemStack(item));

            if (!researchPoints.isEmpty()) {
                rpValues.put(itemName, researchPoints);
            }
        }
    }
    public static String getTeamDataAsString(String teamName) {
        if (!teamRpValues.containsKey(teamName)) {
            return "";
        }

        StringBuilder data = new StringBuilder();
        EnumMap<researchType, Integer> teamData = teamRpValues.get(teamName);

        for (Map.Entry<researchType, Integer> entry : teamData.entrySet()) {
            data.append(entry.getKey()).append("=")
                    .append(entry.getValue()).append(";");
        }

        return data.toString();
    }

    // Add method to load team data from string
    public static void loadTeamDataFromString(String teamName, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        EnumMap<researchType, Integer> teamData = new EnumMap<>(researchType.class);

        String[] pointPairs = data.split(";");
        for (String pair : pointPairs) {
            if (pair.isEmpty()) continue;
            String[] typeAndPoint = pair.split("=");
            if (typeAndPoint.length != 2) continue;

            try {
                researchType type = researchType.valueOf(typeAndPoint[0]);
                int points = Integer.parseInt(typeAndPoint[1]);
                teamData.put(type, points);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        teamRpValues.put(teamName, teamData);
    }
    private double RpMultiplier(Item item) {
        if (item == null) return 1;
        try {
            String name = item.getItemStackDisplayName(new ItemStack(item)).toLowerCase();
            if (name.contains("nugget")) return 0.1111111111;
        } catch (Exception ignored) {}
        return 1;
    }

    private boolean isBlacklisted(Item item) {
        if (item == null) return true;
        String className = item.getClass().getName();
        String name;
        try {
            name = item.getUnlocalizedName();
            if (name == null) return true;
        } catch (Exception e) {
            return true;
        }
        name = name.toLowerCase();
        String[] Keywords = {
                "ladder", "plate", "rod", "pipe",
                "duct", "shell", "wire", "coil",
                "powder", "gear", "barrel",
                "receiver", "stock", "grip",
                "mechanism", "fin", "blade",
                "flywheel", "pole", "anvil",
                "sphere", "beam", "wall", "block",
                "sword", "shovel", "hoe", "pickaxe",
                "drill", "crate", "ore", "billet",
                "helmet", "chestplate", "leggings",
                "box", "boots", "armor", "pylon",
                "capacitor", "crystal", "deco",
                "glass", "bars", "door", "sand",
                "axe", "tank", "scaffold", "corner",
                "pedestal", "port", "carrot", "tile",
                "flint", "furnace"};
        for (String keyword : Keywords) {
            if (name.contains(keyword)) {
                return true;
            }
        }
        if (className.contains("hbm.items.machine") ||
                className.contains("hbm.items.food") ||
                className.contains("hbm.items.tool") ||
                className.contains("hbm.items.armor") ||
                className.contains("hbm.items.weapon") ||
                className.contains("hbm.items.bomb") ||
                className.contains("hbm.items.special")) {
            return true;
        }
        return false;
    }

    public EnumMap<researchType, Integer> getRpValuesForItem(ItemStack item) {
        EnumMap<researchType, Integer> rpMap = new EnumMap<>(researchType.class);
        if (item == null || item.getItem() == null) return rpMap;
        String name = item.getDisplayName().toLowerCase();
        if (rpValues.containsKey(name)) return rpValues.get(name);

        if (name.contains("cadmium") || name.contains("technetium") && !(isBlacklisted(item.getItem()))) {
            rpMap.put(researchType.STRUCTURAL, 35);
        }
        if (name.contains("iron") && !(isBlacklisted(item.getItem()))) {
            rpMap.put(researchType.STRUCTURAL, 10);
        }
        if (name.contains("steel") && !name.contains("cadmium") && !name.contains("technetium") && !(isBlacklisted(item.getItem()))) {
            rpMap.put(researchType.MACHINERY, 40);
            rpMap.put(researchType.STRUCTURAL, 40);
            rpMap.put(researchType.SPACE, 25);
        }
        if (name.contains("uranium") && !(isBlacklisted(item.getItem()))) {
            if (name.contains("238")) {
                rpMap.put(researchType.WEAPONRY, 45);
            }
            if (name.contains("235")) {
                rpMap.put(researchType.NUCLEAR, 50);
            }
            if (name.contains("233")) {
                rpMap.put(researchType.NUCLEAR, 40);
            }
        }
        if (name.contains("plutonium") && !(isBlacklisted(item.getItem()))) {
            if (name.contains("240")) {
                rpMap.put(researchType.NUCLEAR, 40);
            }
            if (name.contains("241")) {
                rpMap.put(researchType.NUCLEAR, 75);
            }
            if (name.contains("238")) {
                rpMap.put(researchType.NUCLEAR, 60);
            }
            if (name.contains("239")) {
                rpMap.put(researchType.EXPLOSIVES, 75);
                rpMap.put(researchType.NUCLEAR, 25);
            }
            if (name.contains("uel")) {
                rpMap.put(researchType.NUCLEAR, 60);
            }
            if (name.contains("reactor")) {
                rpMap.put(researchType.NUCLEAR, 70);
            } else {
                rpMap.put(researchType.NUCLEAR, 25);
            }
        }
        if (name.contains("thorium") && !(isBlacklisted(item.getItem()))) {
            if (name.contains("232")) {
                rpMap.put(researchType.NUCLEAR, 40);
            }
            if (name.contains("uel")) {
                rpMap.put(researchType.NUCLEAR, 50);
            }
        }
        if (name.contains("digamma") && !(isBlacklisted(item.getItem()))) {
            rpMap.put(researchType.EXOTIC, 100);
        }
        if (name.contains("americium")) {
            if (name.contains("241") || name.contains("242")) {
                rpMap.put(researchType.NUCLEAR, 60);
            } else if (name.contains("reactor grade") || name.contains("fuel")) {
                rpMap.put(researchType.NUCLEAR, 70);
            }
        }
        if (name.contains("radium")) {
            rpMap.put(researchType.NUCLEAR, 50);
        }
        if (name.contains("polonium")) {
            rpMap.put(researchType.NUCLEAR, 60);
        }
        if (name.contains("gold") && name.contains("198")) {
            rpMap.put(researchType.NUCLEAR, 30);
        }
        if (name.contains("lead") && name.contains("209")) {
            rpMap.put(researchType.NUCLEAR, 30);
        }
        if (name.contains("strontium") && name.contains("90")) {
            rpMap.put(researchType.NUCLEAR, 40);
        }
        if (name.contains("cobalt") && name.contains("60")) {
            rpMap.put(researchType.NUCLEAR, 50);
        }
        if (name.contains("neptunium")) {
            rpMap.put(researchType.NUCLEAR, 60);
        }
        if (name.contains("curium")) {
            rpMap.put(researchType.NUCLEAR, 20);
        }
        if (name.contains("iridium")) {
            rpMap.put(researchType.MACHINERY, 5);
        }
        if (name.contains("bscco")) {
            rpMap.put(researchType.MACHINERY, 5);
        }
        if (name.contains("stainless steel")) {
            rpMap.put(researchType.MACHINERY, 30);
        }
        if (name.contains("nickel")) {
            rpMap.put(researchType.MACHINERY, 25);
        }
        if (name.contains("bismuth") && name.contains("arsenic bronze")) {
            rpMap.put(researchType.MACHINERY, 35);
        }
        if (name.contains("arsenic")) {
            rpMap.put(researchType.MACHINERY, 20);
        }
        if (name.contains("calcium")) {
            rpMap.put(researchType.CHEMICAL, 10);
        }
        if (name.contains("tantalium")) {
            rpMap.put(researchType.ELECTRONICS, 40);
        }
        if (name.contains("beryllium")) {
            rpMap.put(researchType.MACHINERY, 45);
        }
        if (name.contains("cobalt")) {
            rpMap.put(researchType.MACHINERY, 30);
        }
        if (name.contains("boron")) {
            rpMap.put(researchType.MACHINERY, 25);
            rpMap.put(researchType.NUCLEAR, 30);
        }
        if (name.contains("graphite")) {
            rpMap.put(researchType.MACHINERY, 20);
            rpMap.put(researchType.NUCLEAR, 30);
        }
        if (name.contains("firebrick")) {
            rpMap.put(researchType.MACHINERY, 15);
        }
        if (name.contains("high speed steel") || name.contains("durasteel")) {
            rpMap.put(researchType.MACHINERY, 50);
        }
        if (name.contains("polymer") || name.contains("bakelite")) {
            rpMap.put(researchType.CHEMICAL, 30);
        }
        if (name.contains("latex")) {
            rpMap.put(researchType.CHEMICAL, 15);
        }
        if (name.contains("rubber")) {
            rpMap.put(researchType.CHEMICAL, 20);
        }
        if (name.contains("hard plastic") || name.contains("pvc")) {
            rpMap.put(researchType.CHEMICAL, 25);
        }
        if (name.contains("tungsten")) {
            rpMap.put(researchType.MACHINERY, 60);
        }
        if (name.contains("crystalline fullerite")) {
            rpMap.put(researchType.EXOTIC, 80);
        }
        if (name.contains("advanced alloy")) {
            rpMap.put(researchType.MACHINERY, 100);
        }
        if (name.contains("minecraft grade copper")) {
            rpMap.put(researchType.MACHINERY, 40);
        }
        if (name.contains("gallium arsenide")) {
            rpMap.put(researchType.ELECTRONICS, 60);
        }
        if (name.contains("lanthanium")) {
            rpMap.put(researchType.ELECTRONICS, 70);
        }
        if (name.contains("titanium")) {
            rpMap.put(researchType.MACHINERY, 80);
            rpMap.put(researchType.STRUCTURAL, 60);
        }
        if (name.contains("desh")) {
            rpMap.put(researchType.MACHINERY, 90);
        }
        if (name.contains("ferrouranium")) {
            rpMap.put(researchType.MACHINERY, 30);
        }
        if (name.contains("starmetal")) {
            rpMap.put(researchType.MACHINERY, 100);
        }
        if (name.contains("niobium")) {
            rpMap.put(researchType.ELECTRONICS, 30);
        }
        if (name.contains("bismuth")) {
            rpMap.put(researchType.ELECTRONICS, 65);
        }
        if (name.contains("schrabidium")) {
            rpMap.put(researchType.NUCLEAR, 40);
        }
        if (name.contains("magnetized tungsten")) {
            rpMap.put(researchType.MACHINERY, 60);
        }
        if (name.contains("ferric schrabidate")) {
            rpMap.put(researchType.MACHINERY, 50);
        }
        if (name.contains("solinium")) {
            rpMap.put(researchType.NUCLEAR, 90);
        }
        if (name.contains("actinium")) {
            rpMap.put(researchType.NUCLEAR, 25);
        }
        if (name.contains("australium")) {
            rpMap.put(researchType.NUCLEAR, 80);
        }
        if (name.contains("saturnite")) {
            rpMap.put(researchType.MACHINERY, 70);
        }
        if (name.contains("euphenium")) {
            rpMap.put(researchType.MACHINERY, 200);
        }
        if (name.contains("dineutronium")) {
            rpMap.put(researchType.MACHINERY, 250);
        }
        if (name.contains("electronium")) {
            rpMap.put(researchType.MACHINERY, 300);
        }
        if (name.contains("osmiridium")) {
            rpMap.put(researchType.MACHINERY, 250);
        }
        if (name.contains("hafnium")) {
            rpMap.put(researchType.MACHINERY, 100);
        }
        if (name.contains("chinesium")) {
            rpMap.put(researchType.STRUCTURAL, -9999999);
        }

        return rpMap;
    }

    public static int getRpForType(String itemName, researchType type) {
        return rpValues.getOrDefault(itemName, new EnumMap<>(researchType.class)).getOrDefault(type, 0);
    }

    public static void setServer(MinecraftServer server) {
        minecraftServer = server;
    }
    public static MinecraftServer getServer() {
        return minecraftServer;
    }
    
    public static void saveRp(String team) {
        try {
            
            ISaveHandler saveHandler = minecraftServer.getEntityWorld().getSaveHandler();
            File worldDir = saveHandler.getWorldDirectory();
            File rpDir = new File(worldDir, "hbmenhanced");
            if (!rpDir.exists()) {
                rpDir.mkdirs();
            }

            File teamFile = new File(rpDir, team + ".dat");
            if (!teamFile.exists()) {
                teamFile.createNewFile();
            }
            FileWriter writer = new FileWriter(teamFile);
            EnumMap<researchType, Integer> teamData = teamRpValues.get(team);
            if (teamData != null) {
                for (Map.Entry<researchType, Integer> entry : teamData.entrySet()) {
                    writer.write(entry.getKey().name() + "=" + entry.getValue() + "\n");
                    System.out.println(entry.getKey().name() + "=" + entry.getValue());
                }
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static EnumMap<researchType, Integer> loadRp(String team) {
        EnumMap<researchType, Integer> teamData = new EnumMap<>(researchType.class);
        try {
            ISaveHandler handler = minecraftServer.getEntityWorld().getSaveHandler();
            File worldDir = handler.getWorldDirectory();
            File rpDir = new File(worldDir, "hbmenhanced");
            File teamFile = new File(rpDir, team + ".dat");
            if (teamFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(teamFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("=");
                        if (parts.length == 2) {
                            try {
                                researchType type = researchType.valueOf(parts[0].trim());
                                int points = Integer.parseInt(parts[1].trim());
                                teamData.put(type, points);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error parsing line: " + line);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Store loaded data in memory
                if (!teamData.isEmpty()) {
                    teamRpValues.put(team, teamData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teamData;
    }

    public static void addResearchPoints(String teamName, researchType type, int points) {
        teamRpValues.putIfAbsent(teamName, new EnumMap<>(researchType.class));
        teamRpValues.get(teamName).merge(type, points, Integer::sum);
        saveRp(teamName);
    }
}