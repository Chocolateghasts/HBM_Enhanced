package com.mewo.hbmenhanced.ResearchBlocks.ResearchSource;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.*;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.ResearchNetwork;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileEntityResearchSource extends TileEntity implements IResearchProvider {
    private DirPos dirPos;
    private AbstractNetwork<?> network;
    public NetworkNodeType type;

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
    public void setNetwork(AbstractNetwork<?> network) {
        this.network = network;
    }

    @Override
    public void setDirPos(DirPos dirPos) {
        this.dirPos = dirPos;
    }

    @Override
    public void setType(NetworkNodeType type) {
        this.type = type;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
}
