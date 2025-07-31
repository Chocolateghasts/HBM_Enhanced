package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;

import java.util.List;

public interface IConnectableNode {
    // Properties
    BlockPos getPos();
    DirPos getDirPos();
    ResearchNetwork getNetwork();

    List<BlockPos> getNeighbors();

    // Functions
    void setNetwork(ResearchNetwork network);
    void setDirPos(DirPos dirPos);

}
