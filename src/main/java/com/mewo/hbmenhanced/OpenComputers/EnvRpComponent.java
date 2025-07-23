package com.mewo.hbmenhanced.OpenComputers;

import com.mewo.hbmenhanced.OpenComputers.Util.DriveManager;
import com.mewo.hbmenhanced.OpenComputers.Util.JavaToLua;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.Result;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import li.cil.oc.server.component.Drive;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class EnvRpComponent extends ManagedEnvironment {
    private final ItemStack stack;
    public String team;
    private ResearchTree tree;
    private World world;
    public EnvironmentHost host;

    public EnvRpComponent(ItemStack stack, EnvironmentHost host) {
        this.host = host;
        this.stack = stack;
        this.setNode(Network.newNode(this, Visibility.Network)
                .withComponent("research_component")
                .create());
    }

    public World getWorldthing() {
        if (host instanceof TileEntity) {
            return ((TileEntity) host).getWorldObj();
        }
        return null;
    }

    @Callback
    public Object[] init(Context c, Arguments args) {
        world = getWorldthing();
        Drive drive = DriveManager.getFirstDrive(node());
        if (drive == null) {
            return new Object[]{false, "Could not find unmanaged drive component in computer"};
        }
        String team1 = args.checkString(0);
        if (team1 == null || team1.isEmpty()) {
            return new Object[]{false, "Enter a valid team"};
        }
        this.team = team1;
        ItemStack itemStack = DriveManager.getFirstDriveItem(node(), this.host);
        if (itemStack == null) {
            return new Object[]{false, "Could not find unmanaged drive item in computer"};
        }
        NBTTagCompound nbt = itemStack.getTagCompound();
        if (nbt == null) {
            return new Object[]{false, "how did you not create nbt on the item or im dumb idk"};
        }
        nbt.setString("oc:lock", team);
        nbt.setBoolean("hbm_e:rp_drive", true);
        tree = ResearchTree.getOrCreate(team);
        return new Object[]{true, "Set drive team to " + team};
    }

    @Callback
    public Object[] getAllNodesForTree(Context c, Arguments args) {
        ResearchTree tree = ResearchTree.getOrCreate(team);
        if (tree.nodes.isEmpty()) {
            return new Object[]{false, "Tree is empty"};
        }
        Map<String, Map<String, Object>> nodes = new HashMap<>();
        for (Map.Entry<String, ResearchNode> entry : tree.nodes.entrySet()) {
            Map<String, Object> node = JavaToLua.nodeToLua(entry.getValue());
            nodes.put((String) node.get("id"), node);
        }
        return new Object[]{true, nodes};
    }

    @Callback
    public Object[] unlock(Context c, Arguments args) {
        String id = args.checkString(0);
        ResearchNode node = tree.getNode(id);
        if (node != null) {
            Result res = node.unlock(team, world);
            if (res.isSuccess()) {
                tree.save();
            }
            return new Object[]{res.isSuccess(), res.getMessage()};
        }
        return new Object[]{false, "Node '" + id + "' is not registered"};
    }


}
