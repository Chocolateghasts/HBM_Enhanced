package com.mewo.hbmenhanced.ResearchBlocks.ResearchSource;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IConnectableNode;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchProvider;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.ResearchNetwork;
import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileEntityResearchSource extends TileEntity implements IResearchProvider {
    private DirPos dirPos;
    public ResearchNetwork network;

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
}
