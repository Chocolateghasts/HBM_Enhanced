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
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import com.mewo.hbmenhanced.commands.RPCommand;
import li.cil.oc.common.item.data.DriveData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
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

            int byteOffset = args.checkInteger(0); // Will throw if not a number
            String filePath = args.checkString(1); // Will throw if not a string

            System.out.println("storeRp called with byteOffset: " + byteOffset + ", filePath: " + filePath);

            handleFileSystem(context, args);
            handleDrive(context, args);
            return new Object[]{"Success: File System Handled"};
        } catch (IllegalArgumentException e) {
            return new Object[]{"Error: " + e.getMessage()};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{"Error: Unexpected error occurred"};
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

    private void handleDrive(Context context, Arguments args) {
        Drive drive = getDrive();
        if (drive == null) {
            System.out.println("No drive found.");
            return;
        }

        try {
            int byteOffset = args.checkInteger(0);
            String filePath = args.checkString(1);

            //lockDrive(context, args, drive);

            if (drive.isLocked()) {
                Object[] data = drive.readByte(context, args);
                if (data != null && data.length > 0 && data[0] instanceof byte[]) {
                    byte[] newData = (byte[]) data[0];
                    String content = new String(newData);
                    System.out.println("Read content: " + content);
                }

                String newContent = "Placeholder";
                byte[] byteData = newContent.getBytes();
                Object[] writeResult = drive.writeByte(context, args);

                if (writeResult != null && writeResult.length > 0 && writeResult[0] instanceof Boolean && (Boolean)writeResult[0]) {
                    System.out.println("Write successful");
                } else {
                    System.out.println("Write failed");
                }
            }
        } catch (Exception e) {
            System.out.println("Drive handling error: " + e.getMessage());
            e.printStackTrace();
        }
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



    public static void changeLocked(ItemStack driveItem, boolean locked, String username) {
        if(driveItem == null) {return;}
        NBTTagCompound nbt = driveItem.getTagCompound();
        if (nbt == null) {nbt = new NBTTagCompound();}


        if (!locked) {
            nbt.setString("oc:lock", username);
        } else if (locked) {
            nbt.setString("oc:lock", null);
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
        // Connection handling
    }

    @Override
    public void onDisconnect(Node node) {
        // Disconnection handling
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