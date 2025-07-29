package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;

public interface IConnectableNode {
    // Properties
    BlockPos getPos();
    DirPos getDirPos();
    ResearchNetwork getNetwork();

    BlockPos[] getNeighbors();

    // Functions
    void setNetwork(ResearchNetwork network);
    void setDirPos(DirPos dirPos);

}
