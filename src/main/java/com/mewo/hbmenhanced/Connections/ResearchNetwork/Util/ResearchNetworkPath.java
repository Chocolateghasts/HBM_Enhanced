package com.mewo.hbmenhanced.Connections.ResearchNetwork.Util;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IConnectableNode;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchProvider;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchReceiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ResearchNetworkPath {
    private static final Logger LOGGER = LogManager.getLogger(ResearchNetworkPath.class);

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

    public boolean contains(IConnectableNode node) {
        if (node == null) {
            return false;
        }
        BlockPos nodePos = node.getPos();
        return steps.stream().anyMatch(step ->
                step.getX() == nodePos.getX() &&
                        step.getY() == nodePos.getY() &&
                        step.getZ() == nodePos.getZ());
    }

}
