package com.mewo.hbmenhanced.ResearchBlocks.ResearchCable;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IConnectableNode;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkNodeType;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.AbstractNetwork;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.ResearchNetwork;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.ResearchNetworkManager;
import com.mewo.hbmenhanced.Packets.ConnectionsPacket;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileEntityResearchCable extends TileEntity implements IConnectableNode {
    private DirPos dirPos;
    public EnumMap<ForgeDirection, Boolean> connections = new EnumMap<>(ForgeDirection.class);
    private AbstractNetwork<?> network;
    private NetworkNodeType type;

    public TileEntityResearchCable() {
        this(NetworkNodeType.RESEARCH);
    }

    public TileEntityResearchCable(NetworkNodeType type) {
        this.type = type;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            connections.put(dir, false);
        }
    }

    public void updateConnections() {
        if (worldObj.isRemote) return;
        boolean changed = false;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(
                    xCoord + dir.offsetX,
                    yCoord + dir.offsetY,
                    zCoord + dir.offsetZ
            );

            boolean connected = te instanceof IConnectableNode;
            Boolean previous = connections.get(dir);
            if (previous == null || previous != connected) {
                connections.put(dir, connected);
                changed = true;
            }
        }
        hbmenhanced.network.sendToAllAround(
                new ConnectionsPacket(xCoord, yCoord, zCoord, this.connections),
                new NetworkRegistry.TargetPoint(
                        worldObj.provider.dimensionId,
                        xCoord, yCoord, zCoord,
                        64.0D
                )
        );
    }

    public void updateConnectionsClient(Map<ForgeDirection, Boolean> connections) {
        if (worldObj.isRemote) {
            this.connections.clear();
            this.connections.putAll(connections);
        }
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(xCoord, yCoord, zCoord);
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
        System.out.println("Changed type");
        setNetwork(ResearchNetworkManager.getNetwork(worldObj, this.type));
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public List<BlockPos> getNeighbors() {
        List<BlockPos> pos = new ArrayList<>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(
                    xCoord + dir.offsetX,
                    yCoord + dir.offsetY,
                    zCoord + dir.offsetZ);
            if (te instanceof IConnectableNode && ((IConnectableNode) te).getType() == this.type) {
                pos.add(new BlockPos(te));
            }
        }
        return pos;
    }

    @Override
    public void setNetwork(AbstractNetwork<?> network) {
        this.network = network;
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
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.type == null) {
            this.type = NetworkNodeType.RESEARCH;
        }
        compound.setString("nodeType", this.type.name());
        NBTTagCompound connTag = new NBTTagCompound();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            connTag.setBoolean(dir.name(), connections.get(dir));
        }
        compound.setTag("connections", connTag);
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
        NBTTagCompound connTag = compound.getCompoundTag("connections");
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            connections.put(dir, connTag.getBoolean(dir.name()));
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag); // write everything
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g()); // read everything
    }
}
