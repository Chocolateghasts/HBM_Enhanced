package com.mewo.hbmenhanced.OpenComputers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mewo.hbmenhanced.getRpValue.getRpMap;

public class EnvoirementRPComponent implements ManagedEnvironment {
    private static final Map<Drive, ItemStack> driveItemMap = new HashMap<>();
    private Node node = this.node();
    public boolean isUpdated = false;

    public EnvoirementRPComponent() {
        node = li.cil.oc.api.Network.newNode(this, Visibility.Neighbors)
                .withComponent("RPComponent", Visibility.Neighbors)
                .create();
        System.out.println("RPCOMPONENT registered");
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

    public String getWorldName(World world) {
        if (world.isRemote) {
            return Minecraft.getMinecraft().getIntegratedServer().getWorldName();
        }
        return "unknown_world";
    }

    @Callback(doc = "function(slot:number, teamName:string):table -- Writes RP data to a drive")
    public Object[] writeRp(Context c, Arguments a) {
        try {
            World world = getTileEntity().getWorldObj();
            String worldName = getWorldName(world);
            String filePathString = worldName + "/hbmenhanced/rpData/";
            String team = a.checkString(0);
            int points = a.checkInteger(1);
            String type = a.checkString(2);

            File dir = new File(filePathString);
            dir.mkdirs();
            File rpData = new File(dir, "rpData.json");
            rpData.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Convert type string to enum
            getRpValue.researchType resType;
            try {
                resType = getRpValue.researchType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return new Object[]{"Invalid research type. Must be: STRUCTURAL, NUCLEAR, SPACE, EXPLOSIVES, MACHINERY, WEAPONRY, CHEMICAL, EXOTIC, ELECTRONICS"};
            }

            getRpValue.addRpPoints(team, resType, points);
            Map<String, Object> data = new HashMap<>();

            // Add timestamp and user
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            data.put("timestamp", sdf.format(new Date()));
            data.put("user", "Chocolateghasts");

            // Your existing data
            data.put("team", team);
            data.put("researchType", type);
            data.put("points", points);
            data.put("totalPoints", getRpValue.getTeamResearchPoints(team, resType));

            String jsonLine = gson.toJson(data);

            FileWriter writer = new FileWriter(rpData, true);
            writer.write(jsonLine + "\n");
            writer.close();
            return new Object[]{"Created File"};

        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return new Object[]{"it broke"};
    }

    @Callback(doc = "function(teamName:string):table -- Reads RP data for a team")
    public Object[] readRpData(Context c, Arguments a) {
        try {
            World world = getTileEntity().getWorldObj();
            String worldName = getWorldName(world);
            String filePathString = worldName + "/hbmenhanced/rpData/";
            File rpFile = new File(filePathString + "rpData.json");

            BufferedReader reader = new BufferedReader(new FileReader(rpFile));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            Gson gson = new Gson();
            return new Object[]{content.toString()};
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Object[]{"it broke"};
    }

    @Callback(doc = "function():number -- Get drive capacity usage")
    public Object[] getDriveUsage(Context c, Arguments a) {
        try {
            Drive drive = getDrive();
            if (drive == null) return new Object[]{-1};

            // Get the drive's capacity
            Object[] capacity = drive.getCapacity(c, a);
            if (capacity.length > 0 && capacity[0] instanceof Number) {
                return new Object[]{((Number) capacity[0]).longValue()};
            }

            return new Object[]{-1};
        } catch (Exception e) {
            return new Object[]{-1};
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
                if (nbt.hasKey("oc:data") || nbt.hasKey("hbmenhanced:rpdrive")) {
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

    private Drive getDrive() {
        for (Node connectedNode : node.network().nodes()) {
            if (connectedNode.host() instanceof Drive) {
                return (Drive) connectedNode.host();
            }
        }
        return null;
    }

    private FileSystem getFileSystem() {
        for (Node connectedNode : node.network().nodes()) {
            if (connectedNode.host() instanceof FileSystem) {
                return (FileSystem) connectedNode.host();
            }
        }
        return null;
    }

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