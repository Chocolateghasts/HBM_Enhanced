/*
 * Created by: Chocolateghasts
 * Last modified: 2025-03-09 00:56:21 UTC
 *
 * OpenComputers component for managing RP data
 */

package com.mewo.hbmenhanced.OpenComputers;

import li.cil.oc.api.Items;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.Drive;
import li.cil.oc.server.component.FileSystem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import com.mewo.hbmenhanced.commands.RPCommand;
import li.cil.oc.common.item.data.DriveData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.lwjgl.Sys;
import org.lwjgl.opengl.NVBindlessMultiDrawIndirect;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    @Callback(doc = "function(number:byteOffset, string:filePath): string -- Stores RP data to the file system")
    public Object[] storeRp(Context context, Arguments args) {
        try {
            if (args.count() < 2) {
                return new Object[]{"Error: Not enough arguments. Expected (number, string)"};
            }

            Drive drive = getDrive();
            if (drive != null) {
                lockDrive(context, drive); // Pass just context and drive
            }

            handleFileSystem(context, args);
            // handleDrive(context, args);  // This is commented out as requested
            return new Object[]{"Success: File System Handled"};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{"Error: " + e.getMessage()};
        }
    }

    @Callback(doc = "function():table -- Returns a table of player RP data")
    public Object[] getPlayerRP(Context context, Arguments args) {
        Map<String, Integer> rpMap = RPCommand.playerRPMap;
        HashMap<Object, Object> luaTable = new HashMap<>();
        for (Map.Entry<String, Integer> entry : rpMap.entrySet()) {
            luaTable.put(entry.getKey(), entry.getValue());
        }
        return new Object[]{luaTable};
    }

    private FileSystem getFileSystem() {
        for (Node connectedNode : node.network().nodes()) {
            if (connectedNode.host() instanceof FileSystem) {
                return (FileSystem) connectedNode.host();
            }
        }
        return null;
    }

    private Drive getDrive() {
        for (Node connectedNode : node.network().nodes()) {
            if (connectedNode.host() instanceof Drive) {

                return (Drive) connectedNode.host();
            }
        }
        return null;
    }
    private void registerDriveItemStack(Drive drive, ItemStack stack) {
        driveItemMap.put(drive, stack);
    }
    public ItemStack getDriveStack(Drive drive) {
        return driveItemMap.get(drive);
    }

    private void lockDrive(Context context, Drive drive) {
        try {
            java.lang.reflect.Field hostField = drive.getClass().getDeclaredField("host");
            hostField.setAccessible(true);
            Object hostOption = hostField.get(drive);

            java.lang.reflect.Method getMethod = hostOption.getClass().getMethod("get");
            Object host = getMethod.invoke(hostOption);

            if (host instanceof TileEntity) {
                TileEntity tile = (TileEntity) host;
                if (tile instanceof IInventory) {
                    IInventory inventory = (IInventory) tile;

                    for (int i = 0; i < inventory.getSizeInventory(); i++) {
                        ItemStack stack = inventory.getStackInSlot(i);
                        if (stack != null) {
                            Item item = stack.getItem();
                            // Check if it's an OpenComputers multi item (like the drive)
                            if (item.getClass().getName().contains("li.cil.oc")) {
                                // Get the subItem/variant value
                                int variant = stack.getItemDamage(); // In OC this gets the variant
                                System.out.println("Found OC item in slot " + i + " with variant: " + variant);

                                // Check if it's variant 7 (the drive)
                                if (variant == 7) {
                                    System.out.println("Found drive in slot " + i);
                                    changeLocked(stack, !drive.isLocked());
                                    driveItemMap.put(drive, stack);
                                    System.out.println("Drive lock status changed");
                                    return;
                                }
                            }
                        }
                    }
                    System.out.println("Drive not found in inventory");
                }
            }

        } catch (Exception e) {
            System.out.println("Error accessing drive host: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void handleDrive(Context context, Arguments args) {
        Drive drive = getDrive();
        lockDrive(context, drive);
        if (drive == null) {
            System.out.println("No drive found.");
            return;
        }

//
//        try {
//            int byteOffset = args.checkInteger(0);
//            String filePath = args.checkString(1);
//
//            //lockDrive(context, args, drive);
//
//            if (drive.isLocked()) {
//                Object[] data = drive.readByte(context, args);
//                if (data != null && data.length > 0 && data[0] instanceof byte[]) {
//                    byte[] newData = (byte[]) data[0];
//                    String content = new String(newData);
//                    System.out.println("Read content: " + content);
//                }
//
//                String newContent = "Placeholder";
//                byte[] byteData = newContent.getBytes();
//                Object[] writeResult = drive.writeByte(context, args);
//
//                if (writeResult != null && writeResult.length > 0 && writeResult[0] instanceof Boolean && (Boolean)writeResult[0]) {
//                    System.out.println("Write successful");
//                } else {
//                    System.out.println("Write failed");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Drive handling error: " + e.getMessage());
//            e.printStackTrace();
//        }
    }

    private void handleFileSystem(Context context, Arguments args) {
        FileSystem fileSystem = getFileSystem();
        if (fileSystem == null) {
            System.out.println("No filesystem found.");
            return;
        }

        try {
            Object[] readOnlyCheck = fileSystem.isReadOnly(context, args);
            if (readOnlyCheck != null && readOnlyCheck.length > 0 && readOnlyCheck[0] instanceof Boolean && (Boolean)readOnlyCheck[0]) {
                System.out.println("File system is read-only");

                int byteOffset = args.checkInteger(0);
                String filePath = args.checkString(1);

                Object[] data = fileSystem.read(context, args);
                if (data != null && data.length > 0 && data[0] instanceof byte[]) {
                    byte[] byteData = (byte[]) data[0];
                    String content = new String(byteData);
                    System.out.println("File content: " + content);
                }
            } else {
                System.out.println("File system is not read-only");
            }
        } catch (Exception e) {
            System.out.println("File system error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public DriveData getDriveData() {
        Drive drive = getDrive();
        if (drive != null) {
            try {
                // Get the DriveData field from the Drive class
                java.lang.reflect.Field dataField = Drive.class.getDeclaredField("data");
                dataField.setAccessible(true);
                return (DriveData) dataField.get(drive);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ItemStack getDriveStack() {
        DriveData data = getDriveData();
        if (data != null) {
            try {
                // Get the stack field from the DriveData class
                java.lang.reflect.Field stackField = DriveData.class.getDeclaredField("stack");
                stackField.setAccessible(true);
                return (ItemStack) stackField.get(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static void changeLocked(ItemStack driveItem, boolean locked) {
        if(driveItem == null) {return;}
        NBTTagCompound nbt = driveItem.getTagCompound();
        String playerName = nbt.getString("oc:lock:");
        System.out.println("Initial lock is by: " + nbt.getString("oc:lock:"));



        if (!locked) {
            nbt.setString("oc:lock:", playerName);
            System.out.println("Locked by: " + nbt.getString("oc:lock:"));
        } else if (locked) {
            nbt.setString("oc:lock:", "");
            System.out.println("Locked the drive. also if there is a name here it didnt work: " + nbt.getString("oc:lock"));
        }
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
                java.lang.reflect.Field parentField = drive.getClass().getDeclaredField("_parent");
                parentField.setAccessible(true);
                Object parent = parentField.get(drive);
                if (parent instanceof TileEntity) {
                    TileEntity entity = (TileEntity) parent;
                    if (entity instanceof IInventory) {
                        IInventory inventory = (IInventory) entity;
                        for (int i = 0; i < inventory.getSizeInventory(); i ++) {
                            ItemStack itemStack = inventory.getStackInSlot(i);
                            if (itemStack != null && itemStack.getItem().getClass().getName().contains("li.cil.oc.common.item.Disk")) {
                                registerDriveItemStack(drive, itemStack);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to get item: " +  e.getMessage());
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
        // Message handling
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