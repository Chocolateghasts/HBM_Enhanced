package com.mewo.hbmenhanced.ResearchBlocks.ResearchCable;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IConnectableNode;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.ResearchNetwork;
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
    public ResearchNetwork network;
    public EnumMap<ForgeDirection, Boolean> connections = new EnumMap<>(ForgeDirection.class);

    public TileEntityResearchCable() {
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

    @Override
    public ResearchNetwork getNetwork() {
        return network;
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
    public void setNetwork(ResearchNetwork network) {
        this.network = network;
    }

    @Override
    public void setDirPos(DirPos dirPos) {
        this.dirPos = dirPos;
    }

    @Override
    public void validate() {
        super.validate();
        if (!worldObj.isRemote) {
            hbmenhanced.RESEARCH_NETWORK.add(this);
            setNetwork(hbmenhanced.RESEARCH_NETWORK);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!worldObj.isRemote) {
            hbmenhanced.RESEARCH_NETWORK.remove(this);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound connTag = new NBTTagCompound();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            connTag.setBoolean(dir.name(), connections.get(dir));
        }
        tag.setTag("connections", connTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagCompound connTag = tag.getCompoundTag("connections");
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
