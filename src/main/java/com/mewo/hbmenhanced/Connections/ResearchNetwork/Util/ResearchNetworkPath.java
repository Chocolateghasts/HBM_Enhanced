package com.mewo.hbmenhanced.Connections.ResearchNetwork.Util;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchProvider;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchReceiver;

import java.util.List;

public class ResearchNetworkPath {
    private final List<DirPos> steps;

    public ResearchNetworkPath(List<DirPos> path) {
        this.steps = path;
    }

    public List<DirPos> getSteps() {
        return steps;
    }

    public BlockPos getStart() {
        return steps.isEmpty() ? null : steps.get(0);
    }

    public BlockPos getEnd() {
        return steps.isEmpty() ? null : steps.get(steps.size() - 1);
    }

    public int length() {
        return steps.size();
    }
}
