package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.Util.PathUtil;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.Util.ResearchNetworkPath;
import com.hbm.util.fauxpointtwelve.DirPos;
import javafx.util.Pair;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class ResearchNetwork {
    public static final int MAX_DEPTH = 500;

    public Map<BlockPos, IConnectableNode> nodes = new HashMap<>();
    public Map<BlockPos, IResearchReceiver> endPoints = new HashMap<>();
    public Map<BlockPos, IResearchProvider> sourcePoints = new HashMap<>();

    Map<Pair<BlockPos, BlockPos>, ResearchNetworkPath> pathCache;

    public void update() {

    }

    public boolean isConnected(IResearchProvider provider, IResearchReceiver receiver) {
        Set<BlockPos> marked = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> path = new HashMap<>();

        int checkedNodes = 0;
        BlockPos startPos = provider.getPos();
        BlockPos endPos = receiver.getPos();
        if (startPos.equals(endPos)) {
            return true;
        }
        if (!nodes.containsKey(startPos) || !nodes.containsKey(endPos)) return false;

        queue.add(startPos);
        marked.add(startPos);
        while (!queue.isEmpty()) {
            if (marked.size() >= MAX_DEPTH) return false;
            BlockPos current = queue.poll();
            IConnectableNode node = nodes.get(current);
            if (node == null) {
                continue;
            }

            for (BlockPos neighbor : node.getNeighbors()) {
                if (nodes.get(neighbor) == null) {
                    continue;
                }
                if (!marked.contains(neighbor)) {
                    marked.add(neighbor);
                    path.put(neighbor, current);
                    if (neighbor.equals(endPos)) {
                        ResearchNetworkPath pathResult = PathUtil.buildNetworkPath(path, startPos, endPos);
                        pathCache.put(new Pair<>(startPos, endPos), pathResult);
                        return true;
                    }
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    public void validate() {

    }

    public void transmit() {

    }

    public void add(IConnectableNode node) {
        nodes.putIfAbsent(node.getPos(), node);
        if (node instanceof IResearchProvider) {
            IResearchProvider provider = (IResearchProvider) node;
            sourcePoints.putIfAbsent(provider.getPos(), provider);
        }
        if (node instanceof IResearchReceiver) {
            IResearchReceiver receiver = (IResearchReceiver) node;
            endPoints.putIfAbsent(receiver.getPos(), receiver);
        }
    }

    public void remove(IConnectableNode node) {
        nodes.remove(node.getPos());
        if (node instanceof IResearchProvider) {
            IResearchProvider provider = (IResearchProvider) node;
            sourcePoints.remove(provider.getPos());
        }
        if (node instanceof IResearchReceiver) {
            IResearchReceiver receiver = (IResearchReceiver) node;
            endPoints.remove(receiver.getPos());
        }
    }

    public Map<BlockPos, IConnectableNode> getNodes() {
        return nodes;
    }

}
