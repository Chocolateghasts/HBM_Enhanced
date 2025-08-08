package com.mewo.hbmenhanced.ResearchBlocks.ResearchTerminal;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.*;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.ResearchNetwork;
import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import com.mewo.hbmenhanced.OpenComputers.Util.DriveManager;
import com.mewo.hbmenhanced.OpenComputers.Util.JavaToLua;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.Result;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import li.cil.oc.server.component.Drive;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileEntityResearchTerminal extends TileEntity implements IResearchReceiver, Environment {
    private DirPos dirPos;
    private AbstractNetwork<?> network;
    private NetworkNodeType type;
    public Node node;
    public String team;
    private ResearchTree tree;
    private World world;
    public EnvironmentHost host;

    public TileEntityResearchTerminal() {
        node = Network.newNode(this, Visibility.Network)
                .withComponent("research_terminal")
                .create();
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
        if (nbt.getString("oc:lock") == null || nbt.getString("oc:lock").isEmpty()) {
            nbt.setString("oc:lock", team);
            nbt.setBoolean("hbm_e:rp_drive", true);
            tree = ResearchTree.getOrCreate(team);;
            return new Object[]{true, "Set drive team to " + team};
        } else {
            tree = ResearchTree.getOrCreate(team);
            return new Object[]{false, "Drive is already set!"};
        }
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
    public Object[] getUnlockedNodes(Context c, Arguments args) {
        Map<String, Map<String, Object>> unlockedNodes = new HashMap<>();
        if (tree.nodes == null || tree.nodes.isEmpty()) {
            return new Object[]{false, "No nodes available in tree."};
        }
        for (Map.Entry<String, ResearchNode> entry : tree.nodes.entrySet()) {
            ResearchNode nodeToTest = entry.getValue();
            if (nodeToTest.isUnlocked) {
                Map<String, Object> luaNode = JavaToLua.nodeToLua(nodeToTest);
                unlockedNodes.put(nodeToTest.id, luaNode);
            }
        }
        return new Object[]{true, unlockedNodes};
    }

    @Callback
    public Object[] getLuaNode(Context c, Arguments a) {
        String id = a.checkString(0);
        ResearchNode node = tree.getNode(id);
        if (node == null) {
            return new Object[]{false, "Could not find node " + id};
        }
        Map<String, Object> luaNode = JavaToLua.nodeToLua(node);
        return new Object[]{true, luaNode};
    }

    @Callback
    public Object[] getResearchPoints(Context c, Arguments a) {
        EnumMap<PointManager.ResearchType, Integer> map = PointManager.getAllPoints(team);
        Map<String, Integer> luaMap = new HashMap<>();
        if (map == null || map.isEmpty()) {
            return new Object[]{false, "Could not fetch points for team " + team};
        }
        for (Map.Entry<PointManager.ResearchType, Integer> entry : map.entrySet()) {
            luaMap.put(entry.getKey().name(), entry.getValue());
        }
        return new Object[]{true, luaMap};
    }

    @Callback
    public Object[] getTeam(Context c, Arguments a) {
        return new Object[]{team};
    }

    @Callback
    public Object[] canUnlock(Context c, Arguments a) {
        String id = a.checkString(0);
        ResearchNode node = tree.getNode(id);
        if (node == null) {
            return new Object[]{false, "Could not find node " + id};
        }
        for (String depId : node.dependencies) {
            ResearchNode depNode = tree.getNode(depId);
            if (depNode == null) {
                return new Object[]{false, "Could not find dependencies"};
            }
            if (!depNode.isUnlocked) {
                return new Object[]{false, "Dependency " + depNode.id + " is not unlocked"};
            }
        }
        for (Map.Entry<String, Integer> entry : node.requirements.entrySet()) {
            PointManager.ResearchType type = PointManager.ResearchType.valueOf(entry.getKey());
            int points = PointManager.getPoints(team, type);
            if (points < entry.getValue()) {
                return new Object[]{false, "Not enough points for " + type};
            }
        }
        return new Object[]{true};
    }

    @Callback
    public Object[] getAllIds(Context c, Arguments a) {
        List<String> idList = new ArrayList<>();
        for (Map.Entry<String, ResearchNode> entry : tree.nodes.entrySet()) {
            idList.add(entry.getKey());
        }
        return new Object[]{true, idList};
    }

    @Callback
    public Object[] getDependencies(Context c, Arguments a) {
        String id = a.checkString(0);
        ResearchNode node = tree.getNode(id);
        if (node == null) {
            return new Object[]{false, "Could not find node " + id};
        }
        if (node.dependencies == null) {
            return new Object[]{false, "Dependencies not initialized"};
        }
        return new Object[]{true, node.dependencies};
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

    @Callback
    public Object[] isUnlocked(Context c, Arguments a) {
        String nodeId = a.checkString(0);
        ResearchNode node = tree.getNode(nodeId);
        if (node == null) {
            return new Object[]{false, "Could not find node " + nodeId};
        }
        return new Object[]{true, node.isUnlocked};
    }














    @Override
    public BlockPos getPos() {
        return new BlockPos(this);
    }

    @Override
    public DirPos getDirPos() {
        if (dirPos == null) {
            return new DirPos(getPos().getX(), getPos().getY(), getPos().getZ(), ForgeDirection.UNKNOWN);
        }
        return dirPos;
    }

    public AbstractNetwork<?> getNetwork() {
        if (network == null && worldObj != null) {
            network = (AbstractNetwork<?>) ResearchNetworkManager.getNetwork(worldObj, getType());
        }
        return network;
    }

    @Override
    public NetworkNodeType getType() {
        if (this.type == null) {
            this.type = NetworkNodeType.RESEARCH;
        }
        return this.type;
    }

    @Override
    public void setType(NetworkNodeType type) {
        this.type = type;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void setNetwork(AbstractNetwork<?> network) {
        this.network = network;
    }

    @Override
    public List<BlockPos> getNeighbors() {
        List<BlockPos> pos = new ArrayList<>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(
                    xCoord + dir.offsetX,
                    yCoord + dir.offsetY,
                    zCoord + dir.offsetZ);
            if (te instanceof IConnectableNode) {
                pos.add(new BlockPos(te));
            }
        }
        return pos;
    }

    @Override
    public void setDirPos(DirPos dirPos) {
        this.dirPos = dirPos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validate() {
        super.validate();
        if (!worldObj.isRemote) {
            AbstractNetwork<IConnectableNode> net = (AbstractNetwork<IConnectableNode>) ResearchNetworkManager.getNetwork(worldObj, getType());
            setNetwork(net);
            net.add(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidate() {
        super.invalidate();
        if (!worldObj.isRemote && getNetwork() != null) {
            AbstractNetwork<IConnectableNode> net = (AbstractNetwork<IConnectableNode>) ResearchNetworkManager.getNetwork(worldObj, getType());
            net.remove(this);
            node.remove();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.type == null) {
            this.type = NetworkNodeType.RESEARCH;
        }
        compound.setString("nodeType", this.type.name());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        String nodeTypeString = compound.getString("nodeType");

        if (nodeTypeString == null || nodeTypeString.isEmpty()) {
            // fallback to a default type if missing
            this.type = NetworkNodeType.RESEARCH;
        } else {
            try {
                this.type = NetworkNodeType.valueOf(nodeTypeString);
            } catch (IllegalArgumentException e) {
                // log error, fallback to default to avoid crash
                this.type = NetworkNodeType.RESEARCH;
                // Optionally log the error:
                System.err.println("Invalid nodeType in NBT: " + nodeTypeString);
            }
        }
    }

    @Override
    public Node node() {
        return node;
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
