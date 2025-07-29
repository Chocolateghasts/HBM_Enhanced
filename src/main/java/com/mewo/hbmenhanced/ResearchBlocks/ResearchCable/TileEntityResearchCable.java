package com.mewo.hbmenhanced.ResearchBlocks.ResearchCable;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IConnectableNode;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.ResearchNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityResearchCable extends TileEntity implements IConnectableNode {
    private DirPos dirPos;

    @Override
    public BlockPos getPos() {
        return new BlockPos(xCoord, yCoord, zCoord);
    }

    @Override
    public DirPos getDirPos() {
        // Return cached DirPos; if null, fallback to position + unknown direction
        if (dirPos == null) {
            return new DirPos(getPos().getX(), getPos().getY(), getPos().getZ(), ForgeDirection.UNKNOWN);
        }
        return dirPos;
    }

    @Override
    public ResearchNetwork getNetwork() {
        return null;
    }

    @Override
    public BlockPos[] getNeighbors() {
        return new BlockPos[0];
    }

    @Override
    public void setNetwork(ResearchNetwork network) {

    }

    @Override
    public void setDirPos(DirPos dirPos) {
        this.dirPos = dirPos;
    }
}
