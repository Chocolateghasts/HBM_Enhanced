package com.mewo.hbmenhanced.OpenComputers;

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

    @Callback
    public Object[] getRp(Context c, Arguments a) {
        return new Object[]{"Placeholder"};
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