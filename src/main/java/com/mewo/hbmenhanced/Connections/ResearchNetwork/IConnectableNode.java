package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.hbm.util.fauxpointtwelve.BlockPos;

public interface IConnectableNode {
    // Properties
    BlockPos getPos();
    ResearchNetwork getNetwork();

    BlockPos[] getNeighbors();

    // Functions
    void setNetwork(ResearchNetwork network);

}
