package com.mewo.hbmenhanced.OpenComputers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.Drive;
import li.cil.oc.server.component.FileSystem;
import net.minecraft.nbt.NBTTagCompound;
import com.mewo.hbmenhanced.commands.RPCommand;
import li.cil.oc.common.item.data.DriveData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.Sys;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EnvoirementRPComponent implements ManagedEnvironment {
    private Node node = this.node();
    //Declare the node variable
    public boolean isUpdated = false;
    public EnvoirementRPComponent() {

        node = li.cil.oc.api.Network.newNode(this, Visibility.Neighbors)
                .withComponent("RPComponent", Visibility.Neighbors)
                .create();
        System.out.println("RPCOMPONENT registered");
    }
    @Callback(doc = "function(): string; Stores RP data to the file system.")
    public Object[] storeRp(Context context, Arguments args) {
        handleFileSystem(context, args);
        handleDrive(context, args);
        return new Object[]{"File System Handled."};
    }
    @Callback
    public Object[] getPlayerRP(Context context, Arguments args) {

        Map<String, Integer> rpMap = RPCommand.playerRPMap;

        HashMap<Object, Object> luaTable = new HashMap<>();
        for (Map.Entry<String, Integer> entry : rpMap.entrySet()) {
            luaTable.put(entry.getKey(), entry.getValue());
        }
        return new Object[]{luaTable};
    }

    public FileSystem getFileSystem() {
        for (Node connectedNode : node.network().nodes()) {
            System.out.println("Node is: " + connectedNode);
            if (connectedNode instanceof FileSystem) {
                FileSystem fileSystem = (FileSystem) connectedNode;
                System.out.println("FS is: " + fileSystem);
                return fileSystem;
            }
        }
        return null;
    }
    public Drive getDrive() {
        for (Node connectedNode : node.network().nodes()) {
            if (connectedNode instanceof Drive) {
                System.out.println("is drive");
                Drive drive = (Drive) connectedNode;
                return drive;
            }
        }
        return null;
    }
    private void handleDrive(Context context, Arguments args) {
        Drive drive = getDrive();
        if (drive == null) {
            System.out.println("No drive found.");
            return; // Exit early if no drive is found
        }

        lockDrive(context, args, drive);

        try {
            if (drive.isLocked()) {
                String filePath = args.optString(0, "/rpStorage/file.txt");
                Object[] data = drive.readByte(context, args);
                if (data != null && data.length > 0 && data[0] instanceof byte[]) {
                    byte[] newData = (byte[]) data[0];
                    String content = new String(newData);
                    System.out.println("Content: " + content);
                }
            }

            String newContent = "Placeholder";
            byte[] byteData = newContent.getBytes();
            Object[] doesWrite = drive.writeByte(context, args);

            if (doesWrite != null && ((Boolean) doesWrite[0])) {
                System.out.println("Write successful.");
            } else {
                System.out.println("Cannot write.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lockDrive(Context context, Arguments arguments, Drive drive) {
        System.out.println("Label is: " + drive.getLabel(context, arguments));

    }
    private void unlockDrive(Drive drive) throws NoSuchFieldException, IllegalAccessException {

    }
    private void handleFileSystem(Context context, Arguments args) {
        FileSystem fileSystem = getFileSystem();
        if (fileSystem != null) {
            try {
                Object[] readOnlyCheck = fileSystem.isReadOnly(context, args);

                if (readOnlyCheck != null && ((Boolean) readOnlyCheck[0])) {
                    System.out.println("The file system is in read-only mode.");
                    String filepath = args.optString(0, "/rpStorage/rpStorage.txt");
                    Object[] data = fileSystem.read(context, args);
                    if (data != null && data.length > 0 && data[0] instanceof byte[]) {
                        byte[] byteData = (byte[]) data[0];  // Extract the byte array
                        String content = new String(byteData);  // Convert to string
                        System.out.println("Content from file: " + content);
                    } else {
                        System.out.println("Error: Data read is invalid or empty.");
                    }
                } else {
                    System.out.println("Error: The file system is not read-only, which shouldn't be the case.");
                }
            } catch (Exception e) {
                // Catch any other unexpected errors
                System.out.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();  // Log full stack trace for debugging
            }
        } else {
            System.out.println("No filesystem found.");
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

    //String content = new String(byteData);
    //System.out.println("Content: " + content);
    //Object[] readOnlyCheck = fileSystem.isReadOnly(context, args);
    //if (readOnlyCheck != null && ((Boolean) readOnlyCheck[0])) {

    //    byte[] byteDataToWrite = newData.getBytes();
    //    fileSystem.write(context, args, byteDataToWrite);
    //}

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

    @Override
    public Node node() {
        return node; // Return the node
    }

    @Override
    public void onConnect(Node node) {
    }

    @Override
    public void onDisconnect(Node node) {
    }

    @Override
    public void onMessage(Message message) {
    }
}
