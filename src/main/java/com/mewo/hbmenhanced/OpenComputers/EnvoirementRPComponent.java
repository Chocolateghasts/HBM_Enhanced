package com.mewo.hbmenhanced.OpenComputers;

import com.hbm.blocks.generic.BlockDirt;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.ChemplantRecipes;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemAssemblyTemplate;
import com.hbm.items.machine.ItemChemistryTemplate;
import com.hbm.items.machine.ItemCrucibleTemplate;
import com.mewo.hbmenhanced.getRpValue;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.Drive;
import li.cil.oc.server.component.FileSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import scala.util.control.TailCalls;
import com.hbm.inventory.recipes.AssemblerRecipes;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.mewo.hbmenhanced.getRpValue.getRpMap;
import static com.mewo.hbmenhanced.getRpValue.getServer;
import static java.lang.Math.round;

public class EnvoirementRPComponent implements ManagedEnvironment {
    private static final Map<Drive, ItemStack> driveItemMap = new HashMap<>();
    private Node node = this.node();
    public boolean isUpdated = false;

    public EnvoirementRPComponent() {
        node = li.cil.oc.api.Network.newNode(this, Visibility.Neighbors)
                .withComponent("RPComponent", Visibility.Neighbors)
                .create();
        //System.out.println("RPCOMPONENT registered");
    }

    private TileEntity getTileEntity() {
        Drive drive = getDrive();
        if (drive == null) return null;
        try {
            java.lang.reflect.Field hostField = drive.getClass().getDeclaredField("host");
            hostField.setAccessible(true);
            Object hostOption = hostField.get(drive);
            java.lang.reflect.Method getMethod = hostOption.getClass().getMethod("get");
            Object host = getMethod.invoke(hostOption);

            if (host instanceof TileEntity) {
                return (TileEntity) host;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private ItemStack getDriveItem(Drive drive, int slot) {

        ItemStack stack = driveItemMap.get(drive);
        if (stack != null) {
            //System.out.println("getDriveItem: Found drive in map");
            return stack;
        }

        // If not in map, check inventory
        TileEntity tile = getTileEntity();
        if (!(tile instanceof IInventory)) {
            //System.out.println("getDriveItem: Tile entity is not an inventory");
            return null;
        }

        IInventory inventory = (IInventory) tile;
        //System.out.println("getDriveItem: Searching inventory of size " + inventory.getSizeInventory());

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null) {

                int variant = itemStack.getItemDamage();
                if (variant == 7 && slot == i) {
                    return itemStack;
                }
            }
        }

        //System.out.println("getDriveItem: No drive item found in inventory");
        return null;
    }
    public void changeLocked(String team, int slot) {
        Drive drive = getDrive();
        ItemStack driveItem = getDriveItem(drive, slot);
        System.out.println(driveItem);
        NBTTagCompound nbt = driveItem.getTagCompound();
        String isLocked = nbt.getString("oc:lock");


        if (isLocked.isEmpty()) {
            System.out.println("Lock string is empty, locking with team: " + nbt.getString("hbmenhanced:team"));
            nbt.setString("oc:lock", nbt.getString("hbmenhanced:team"));
        } else if (isLocked.equals(team)) {
            System.out.println("Lock string matches team, unlocking");
            nbt.setString("oc:lock", "");
            nbt.setString("hbmenhanced:team", team);
        } else {
            System.out.println("No action taken - lock string not empty and doesn't match team");
        }
    }

    @Callback
    public Object[] giveTemplate(Context c, Arguments a) {
        try {
            System.out.println("[DEBUG] Method entered");

            String id = a.checkString(0);
            String playerName = a.checkString(1);

            System.out.println("[DEBUG] id = " + id + ", playerName = " + playerName);

            ResearchTree tree = new ResearchTree(getRpValue.getServer());
            ResearchNode nodeToGive = tree.getNode(id);

            if (nodeToGive == null) {
                System.out.println("[DEBUG] Node not found");
                return new Object[]{"Failed to get node"};
            }

            ServerConfigurationManager scm = getServer().getConfigurationManager();
            EntityPlayerMP player = scm.func_152612_a(playerName);

            if (player == null) {
                System.out.println("[DEBUG] Player not found: " + playerName);
                return new Object[]{"Could not find player"};
            }

            List<Map<String, Object>> templates = nodeToGive.templates;
            if (templates == null || templates.isEmpty()) {
                System.out.println("[DEBUG] No templates found");
                return new Object[]{"No templates found in node"};
            }

            System.out.println("[DEBUG] Templates size: " + templates.size());

            for (Map<String, Object> templateData : templates) {
                String templateType = (String) templateData.get("type");
                Double templateId2 = (Double) templateData.get("id");
                int templateId = (int) Math.round(templateId2);
                System.out.println("[DEBUG] Processing template: type=" + templateType + ", id=" + templateId);

                ItemStack stack;

                if ("a".equalsIgnoreCase(templateType)) {
                    ComparableStack key = AssemblerRecipes.recipeList.get(templateId);
                    if (key == null) {
                        System.out.println("[DEBUG] No recipe for ID: " + templateId);
                        return new Object[]{"No recipe for template ID: " + templateId};
                    }
                    stack = new ItemStack(ModItems.assembly_template, 1, templateId);
                    ItemAssemblyTemplate.writeType(stack, key);
                } else if ("b".equalsIgnoreCase(templateType)) {
                    stack = new ItemStack(ModItems.chemistry_template, 1, templateId);
                } else if ("c".equalsIgnoreCase(templateType)) {
                    stack = new ItemStack(ModItems.crucible_template, 1, templateId);
                } else {
                    return new Object[]{"Unknown template type: " + templateType};
                }

                player.inventory.addItemStackToInventory(stack);
            }

            System.out.println("[DEBUG] Finished processing templates");
            return new Object[]{"Successfully gave Template"};

        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{"Exception occurred: " + e.getMessage()};
        }
    }


    @Callback
    public Object[] getTeam(Context c, Arguments a) {
        Drive drive = getDrive();
        int slot = a.checkInteger(0);
        ItemStack itemStack = getDriveItem(drive, slot);
        NBTTagCompound nbt = itemStack.getTagCompound();
        String team = nbt.getString("oc:lock");
        return new Object[]{team};
    }

    @Callback(doc = "function(teamName:string):table -- Returns the RP data for the specified team")
    public Object[] getRp(Context c, Arguments a) {
        try {
            if (a.count() < 1) {
                return new Object[]{"Error: Expected (teamName)"};
            }

            String team = a.checkString(0).trim();
            changeLocked(team, 6);

            // Explicitly reload the team's data from the .dat file
            EnumMap<getRpValue.researchType, Integer> teamData = getRpValue.loadRp(team);

            if (teamData == null || teamData.isEmpty()) {
                // If no data is found, create a default data structure
                teamData = new EnumMap<>(getRpValue.researchType.class);
                for (getRpValue.researchType type : getRpValue.researchType.values()) {
                    teamData.put(type, 0);
                }
            }

            Map<String, Integer> luaMap = new HashMap<>();
            for (Map.Entry<getRpValue.researchType, Integer> entry : teamData.entrySet()) {
                luaMap.put(entry.getKey().name(), entry.getValue());
            }

            return new Object[]{true, luaMap};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{false, "Error: " + e.getMessage()};
        }
    }

    @Callback
    public Object[] setRp(Context c, Arguments a) {
        try {
            if (a.count() < 2){
                return new Object[]{"Invalid Arguments"};
            }
            String team = a.checkString(0);
            Map<String, Double> rpMap = a.checkTable(1);

            System.out.println("Current team RP before changes: " + getRpValue.getTeamRpMap().get(team));

            for (Map.Entry<String, Double> entry : rpMap.entrySet()) {
                System.out.println("Adding for type " + entry.getKey() + ": " + entry.getValue()); // Debug print
                getRpValue.addResearchPoints(team,
                        getRpValue.researchType.valueOf(entry.getKey()),
                        (int) Math.round(entry.getValue())
                );
                System.out.println("Current values after add: " + getRpValue.getTeamRpMap().get(team)); // Debug print
            }
            return new Object[]{"Success"};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{"Error: " + e.getMessage()};
        }
    }

    @Callback(doc = "function(slot:number):string -- Checks if a drive in the specified slot is valid for initialization")
    public Object[] checkDrive(Context context, Arguments args) {
        try {
            if (args.count() < 1) {
                return new Object[]{"Unspecified Drive. Correct syntax: checkDrive(<slot number>)"};
            }
            int slot = args.checkInteger(0);
            Drive drive = getDrive();

            if (drive == null) {
                return new Object[]{"No drive component found"};
            }

            TileEntity tile = getTileEntity();
            if (!(tile instanceof IInventory)) {
                return new Object[]{"No valid inventory found"};
            }

            IInventory inventory = (IInventory) tile;
            ItemStack item = inventory.getStackInSlot(slot);

            if (item == null) {
                return new Object[]{"No item in slot " + slot};
            }

            int variant = item.getItemDamage();
            NBTTagCompound nbt = item.getTagCompound();

            if (nbt == null) {
                return new Object[]{"Drive has no NBT data"};
            }

            Byte unmanaged = nbt.getByte("oc:unmanaged");
            if (item.getItem().getClass().getName().contains("li.cil.oc") && variant == 7 && unmanaged == 1) {
                if (nbt.hasKey("oc:data")) {
                    return new Object[]{"Drive already has data"};
                }
                if (nbt.hasKey("hbmenhanced:rpdrive")) {
                    return new Object[]{"Already initialized"};
                }
            } else {
                return new Object[]{"No valid Drive in slot " + slot};
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{"Error: " + e.getMessage()};
        }
        return new Object[]{"Drive is valid"};
    }

    @Callback(doc = "function(slot:number, teamName:string):string -- Initializes an RP drive in the specified slot with the given team name")
    public Object[] initializeRpDrive(Context context, Arguments args) {
        try {
            if (args.count() < 2) {
                return new Object[]{"Error: Expected (slot, teamName)"};
            }

            int slot = args.checkInteger(0);
            String teamName = args.checkString(1);

            Drive drive = getDrive();
            if (drive == null) {
                return new Object[]{"No drive component found"};
            }

            TileEntity tile = getTileEntity();
            if (!(tile instanceof IInventory)) {
                return new Object[]{"No valid inventory found"};
            }

            IInventory inventory = (IInventory) tile;
            ItemStack item = inventory.getStackInSlot(slot);

            if (item == null) {
                return new Object[]{"No item in slot " + slot};
            }

            NBTTagCompound nbt = item.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                item.setTagCompound(nbt);
            }

            byte unmanaged = nbt.getByte("oc:unmanaged");
            if (item.getItem().getClass().getName().contains("li.cil.oc") && item.getItemDamage() == 7 && unmanaged == 1) {
                if (nbt.hasKey("hbmenhanced:rpdrive")) {
                    return new Object[]{"Drive already initialized"};
                }

                nbt.setBoolean("hbmenhanced:rpdrive", true);
                nbt.setString("oc:lock", teamName);
                item.setStackDisplayName("§bRP Research Storage Drive");
                item.setTagCompound(nbt);

                return new Object[]{"Successfully initialized drive for team " + teamName};
            }

            return new Object[]{"Invalid drive type"};

        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{"Error: " + e.getMessage()};
        }
    }

    @Callback(doc = "function():table -- Returns all research nodes")
    public Object[] getNodes(Context c, Arguments a) {
        ResearchTree tree = new ResearchTree(getServer());
        List<ResearchNode> nodes = tree.getNodes();

        List<Map<String, Object>> luaNodes = new ArrayList<>();

        for (ResearchNode node : nodes) {
            Map<String, Object> luaNode = new HashMap<>();
            luaNode.put("name", node.name);
            luaNode.put("id", node.id);
            luaNode.put("category", node.category);
            luaNode.put("description", node.description);
            luaNode.put("level", node.level);
            luaNode.put("unlocked", node.unlocked);
            luaNode.put("templates", node.templates);
            luaNode.put("dependencies", node.dependencies);
            luaNode.put("type", node.type.toString());
            Map<String, Integer> luaReqs = new HashMap<>();
            for (Map.Entry<getRpValue.researchType, Integer> entry : node.requirements.entrySet()) {
                luaReqs.put(entry.getKey().name(), entry.getValue());
            }
            luaNode.put("requirements", luaReqs);
            luaNode.put("xPos", node.xPos);
            luaNode.put("yPos", node.yPos);
            luaNodes.add(luaNode);
        }

        return new Object[]{luaNodes};
    }

    @Callback(doc = "function(nodes:table):boolean, string -- Saves all nodes with their current states and properties")
    public Object[] saveNodes(Context c, Arguments a) {
        try {
            Map<String, Object> nodes = a.checkTable(0);
            ResearchTree tree = new ResearchTree(getRpValue.getServer());
            int savedCount = 0;

            for (Map.Entry<String, Object> entry : nodes.entrySet()) {
                if (!(entry.getValue() instanceof Map)) continue;

                ResearchNode node = tree.getNode(entry.getKey());
                if (node == null) continue;

                Map<String, Object> data = (Map<String, Object>) entry.getValue();
                updateNodeFromData(node, data);
                tree.editNode(
                        node.id,
                        node.name,
                        node.description,
                        node.level,
                        node.unlocked,
                        node.category,
                        node.templates,
                        node.dependencies,
                        node.requirements,
                        node.teamUnlocked,
                        node.xPos,
                        node.yPos,
                        node.type
                );
                savedCount++;
            }

            return new Object[]{true, savedCount + " nodes saved"};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{false, "Error: " + e.getMessage()};
        }
    }

    private void updateNodeFromData(ResearchNode node, Map<String, Object> data) {
        if (data.containsKey("name")) node.name = String.valueOf(data.get("name"));
        if (data.containsKey("category")) node.category = String.valueOf(data.get("category"));
        if (data.containsKey("description")) node.description = String.valueOf(data.get("description"));
        if (data.containsKey("level")) node.level = ((Number) data.get("level")).intValue();
        if (data.containsKey("unlocked")) node.unlocked = (Boolean) data.get("unlocked");
        if (data.containsKey("xPos")) node.xPos = ((Number) data.get("xPos")).floatValue();
        if (data.containsKey("yPos")) node.yPos = ((Number) data.get("yPos")).floatValue();
        if (data.containsKey("type")) node.type = getRpValue.researchType.valueOf(data.get("type").toString());
        // Collections
        if (data.containsKey("dependencies") && data.get("dependencies") instanceof Map) {
            node.dependencies.clear();
            ((Map<?, ?>) data.get("dependencies")).values()
                    .forEach(v -> node.dependencies.add(String.valueOf(v)));
        }

        if (data.containsKey("requirements") && data.get("requirements") instanceof Map) {
            node.requirements.clear();
            ((Map<?, ?>) data.get("requirements")).forEach((k, v) ->
                    node.requirements.put(
                            getRpValue.researchType.valueOf(String.valueOf(k)),
                            v instanceof Number ? ((Number) v).intValue() : 0
                    )
            );
        }

        if (data.containsKey("teamUnlocked") && data.get("teamUnlocked") instanceof Map) {
            node.teamUnlocked.clear();
            ((Map<?, ?>) data.get("teamUnlocked")).forEach((k, v) ->
                    node.teamUnlocked.put(String.valueOf(k), Boolean.TRUE.equals(v))
            );
        }
        if (data.containsKey("templates") && data.get("templates") instanceof List) {
            List<Map<String, Object>> templates = (List<Map<String, Object>>) data.get("templates");
            node.templates.clear();  // Clear existing templates

            for (Map<String, Object> templateData : templates) {
                // Ensure that template data is valid
                if (templateData.containsKey("type") && templateData.containsKey("id")) {
                    Map<String, Object> template = new HashMap<>();
                    template.put("type", templateData.get("type"));
                    template.put("id", templateData.get("id"));
                    node.templates.add(template);  // Add the new template to the list
                }
            }
        }
    }

    @Callback
    public Object[] getNode(Context c, Arguments a) {
        String id = a.checkString(0);
        ResearchTree tree = new ResearchTree(getServer());
        ResearchNode researchNode = tree.getNode(id);
        Map<String, Object> luaNode = new HashMap<>();
        luaNode.put("name", researchNode.name);
        luaNode.put("id", researchNode.id);
        luaNode.put("category", researchNode.category);
        luaNode.put("description", researchNode.description);
        luaNode.put("level", researchNode.level);
        luaNode.put("unlocked", researchNode.unlocked);
        luaNode.put("templates", researchNode.templates);
        luaNode.put("dependencies", researchNode.dependencies);
        Map<String, Integer> luaReqs = new HashMap<>();
        for (Map.Entry<getRpValue.researchType, Integer> entry : researchNode.requirements.entrySet()) {
            luaReqs.put(entry.getKey().name(), entry.getValue());
        }
        luaNode.put("teamUnlocked", researchNode.teamUnlocked);
        luaNode.put("requirements", luaReqs);
        luaNode.put("xPos", researchNode.xPos);
        luaNode.put("yPos", researchNode.yPos);
        luaNode.put("type", researchNode.type);
        return new Object[]{luaNode};
    }

    private Drive getDrive() {
        for (Node connectedNode : node.network().nodes()) {
            if (connectedNode.host() instanceof Drive) {
                return (Drive) connectedNode.host();
            }
        }
        return null;
    }

//    private FileSystem getFileSystem() {
//        for (Node connectedNode : node.network().nodes()) {
//            if (connectedNode.host() instanceof FileSystem) {
//                return (FileSystem) connectedNode.host();
//            }
//        }
//        return null;
//    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void update() {
        if (!isUpdated) {
            isUpdated = true;
        }
    }

    @Override
    public Node node() {
        return node;
    }

    @Override
    public void onConnect(Node node) {
        if (node.host() instanceof Drive) {
            Drive drive = (Drive) node.host();
            try {
                java.lang.reflect.Field hostField = drive.getClass().getDeclaredField("host");
                hostField.setAccessible(true);
                Object hostOption = hostField.get(drive);
                java.lang.reflect.Method getMethod = hostOption.getClass().getMethod("get");
                Object host = getMethod.invoke(hostOption);
                if (host instanceof TileEntity && host instanceof IInventory) {
                    IInventory inventory = (IInventory) host;
                    for (int i = 0; i < inventory.getSizeInventory(); i++) {
                        ItemStack itemStack = inventory.getStackInSlot(i);
                        if (itemStack != null && itemStack.getItem().getClass().getName().contains("li.cil.oc.common.item.Disk")) {
                            driveItemMap.put(drive, itemStack);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisconnect(Node node) {
        if (node.host() instanceof Drive) {
            driveItemMap.remove(node.host());
        }
    }

    @Override
    public void onMessage(Message message) {
    }

    @Override
    public void load(NBTTagCompound nbt) {
        if (node != null) {
            node.load(nbt);
        }
    }

    @Override
    public void save(NBTTagCompound nbt) {
        if (node != null) {
            node.save(nbt);
        }
    }
}
//@Callback(doc = "function(slot:number, teamName:string):table -- Writes RP data to a drive")
//public Object[] writeRp(Context c, Arguments a) {
//    System.out.println("Starting");
//    try {
//        System.out.println("Trying");
//        if (a.count() < 2) {
//            return new Object[]{"Error: Expected (slot, teamName)"};
//        }
//
//        int slot = a.checkInteger(0);
//        String teamName = a.checkString(1);
//
//        // Get the team's RP data
//        String rpData = getRpValue.getTeamDataAsString(teamName);
//        System.out.println("Debug: Got RP data for team " + teamName); // Debug line
//
//        Drive drive = getDrive();
//        if (drive == null) return new Object[]{false, "No drive found"};
//        System.out.println("Debug: Found drive"); // Debug line
//
//        TileEntity tile = getTileEntity();
//        if (tile == null) return new Object[]{false, "No tile entity"};
//        System.out.println("Debug: Found tile entity"); // Debug line
//
//        if (!(tile instanceof IInventory)) {
//            return new Object[]{false, "Invalid inventory"};
//        }
//
//        IInventory inventory = (IInventory) tile;
//        ItemStack item = inventory.getStackInSlot(slot);
//
//        if (item == null) {
//            return new Object[]{false, "No item in slot " + slot};
//        }
//        System.out.println("Debug: Found item in slot " + slot); // Debug line
//
//        NBTTagCompound nbt = item.getTagCompound();
//        if (nbt == null) {
//            nbt = new NBTTagCompound();
//            item.setTagCompound(nbt);
//        }
//
//        World world = tile.getWorldObj();
//        ISaveHandler saveHandler = world.getSaveHandler();
//        if (saveHandler instanceof SaveHandler) {
//            File worldDirectory = ((SaveHandler) saveHandler).getWorldDirectory();
//            System.out.println("Debug: World directory: " + worldDirectory.getAbsolutePath()); // Debug line
//
//            File rpDirectory = new File(worldDirectory, "hbmenhanced/rpdata");
//            System.out.println("Debug: RP directory: " + rpDirectory.getAbsolutePath()); // Debug line
//
//            if (!rpDirectory.exists()) {
//                boolean created = rpDirectory.mkdirs();
//                System.out.println("Debug: Creating directory result: " + created); // Debug line
//            }
//
//            // Write the RP data
//            File rpFile = new File(rpDirectory, "rpdata_" + teamName + ".dat");
//            System.out.println("Debug: Attempting to write to file: " + rpFile.getAbsolutePath()); // Debug line
//
//            try (FileWriter writer = new FileWriter(rpFile)) {
//                writer.write(rpData);
//                System.out.println("Debug: Successfully wrote data to file"); // Debug line
//            }
//
//            // Get file size for the Lua gibberish
//            long fileSize = rpFile.length();
//            System.out.println("Debug: File size: " + fileSize); // Debug line
//
//            return new Object[]{true, fileSize};
//        } else {
//            System.out.println("Debug: SaveHandler is not instance of SaveHandler: " + saveHandler.getClass().getName()); // Debug line
//        }
//
//    } catch (Exception e) {
//        System.out.println("Debug: Exception occurred: " + e.getClass().getName()); // Debug line
//        e.printStackTrace();
//        return new Object[]{false, e.getMessage()};
//    }
//    return new Object[]{false, "Unknown error"};
//}
//
//@Callback(doc = "function(teamName:string):table -- Reads RP data for a team")
//public Object[] readRpData(Context c, Arguments a) {
//    try {
//        String teamName = a.checkString(0);
//
//        TileEntity tile = getTileEntity();
//        if (tile == null) return new Object[]{false, "No tile entity"};
//
//        World world = tile.getWorldObj();
//        ISaveHandler saveHandler = world.getSaveHandler();
//        if (!(saveHandler instanceof SaveHandler)) {
//            return new Object[]{false, "Invalid save handler"};
//        }
//
//        File worldDirectory = ((SaveHandler) saveHandler).getWorldDirectory();
//        File rpDirectory = new File(worldDirectory, "hbmenhanced/rpdata");
//        File rpFile = new File(rpDirectory, "rpdata_" + teamName + ".dat");
//
//        if (!rpFile.exists()) {
//            return new Object[]{false, "No data found for team"};
//        }
//
//        // Read the file
//        StringBuilder content = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new FileReader(rpFile))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//        }
//
//        // Load the data back into getRpValue
//        getRpValue.loadTeamDataFromString(teamName, content.toString());
//
//        // Return the data and file size for Lua
//        return new Object[]{true, content.toString(), rpFile.length()};
//
//    } catch (Exception e) {
//        e.printStackTrace();
//        return new Object[]{false, e.getMessage()};
//    }
//}