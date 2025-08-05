package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.ResearchNetwork;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchCable.TileEntityResearchCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Map;

public interface IConnectableNode {
    // Properties
    BlockPos getPos();
    DirPos getDirPos();
    AbstractNetwork<?> getNetwork();
    NetworkNodeType getType();

    List<BlockPos> getNeighbors();

    // Functions
    void setNetwork(AbstractNetwork<?> network);
    void setDirPos(DirPos dirPos);
    void setType(NetworkNodeType type);

    default void setTypeFromNeighbors(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityResearchCable) {
            TileEntityResearchCable cable = ((TileEntityResearchCable) te);
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                boolean isConnected = cable.connections.get(dir);
                if (isConnected) {
                    TileEntity neighbor = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
                    if (neighbor instanceof IConnectableNode) {
                        NetworkNodeType type = ((IConnectableNode) neighbor).getType();
                        if (type == null) {
                            type = NetworkNodeType.RESEARCH;
                        }
                        cable.setType(type);
                        break;
                    }
                }
            }
        }
    }
}
