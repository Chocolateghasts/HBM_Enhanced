package com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.AbstractNetwork;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IConnectableNode;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchProvider;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchReceiver;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.Util.PathUtil;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.Util.ResearchNetworkPath;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerTerminalNetwork  extends AbstractNetwork<IConnectableNode> {
    public static final int MAX_DEPTH = 500;
    private static final Logger LOGGER = LogManager.getLogger(ControllerTerminalNetwork.class);

    private Map<BlockPos, IResearchReceiver> endPoints = new HashMap<>();
    private Map<BlockPos, IResearchProvider> sourcePoints = new HashMap<>();
    private Map<Pair<BlockPos, BlockPos>, ResearchNetworkPath> pathCache = new HashMap<>();

    private <L, R> Pair<L, R> pair(L left, R right) {
        return Pair.of(left, right);
    }

    @Override
    protected void onAdd(IConnectableNode node) {
        if (node instanceof IResearchProvider) {
            sourcePoints.put(node.getPos(), (IResearchProvider) node);
        }
        if (node instanceof IResearchReceiver) {
            endPoints.put(node.getPos(), (IResearchReceiver) node);
        }
    }

    @Override
    protected void onRemove(IConnectableNode node) {
        sourcePoints.remove(node.getPos());
        endPoints.remove(node.getPos());
        pathCache.entrySet().removeIf(e ->
                e.getKey().getLeft().equals(node.getPos()) || e.getKey().getRight().equals(node.getPos())
        );
    }

    @Override
    public void harshUpdate() {
        //LOGGER.info("Path cache size: {}", pathCache.size());

        long startTime = System.nanoTime();

        int changedNodeCount = changedNodes.size();
        int pathsRebuilt = 0;

        while (!changedNodes.isEmpty()) {
            IConnectableNode node = changedNodes.poll();

            List<Pair<BlockPos, BlockPos>> keysToUpdate = new ArrayList<>();
            for (Map.Entry<Pair<BlockPos, BlockPos>, ResearchNetworkPath> entry : pathCache.entrySet()) {
                IConnectableNode n = nodes.get(node.getPos());
                if (n == null) continue;
                if (entry.getValue().contains(n)) {
                    keysToUpdate.add(entry.getKey());
                }
            }

            for (Pair<BlockPos, BlockPos> key : keysToUpdate) {
                IConnectableNode providerNode = nodes.get(key.getLeft());
                IConnectableNode receiverNode = nodes.get(key.getRight());
                if (!(providerNode instanceof IResearchProvider) || !(receiverNode instanceof IResearchReceiver)) {
                    continue;
                }
                ResearchNetworkPath path = PathUtil.createPath(nodes, (IResearchProvider) providerNode, (IResearchReceiver) receiverNode);
                if (path != null) {
                    pathCache.put(key, path);
                } else {
                    pathCache.remove(key);
                }
                pathsRebuilt++;
            }
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        if (changedNodeCount > 0) {
            //LOGGER.info("harshUpdate processed {} changed nodes, rebuilt {} paths in {} ms", changedNodeCount, pathsRebuilt, durationMs);
            if (durationMs > 50) {
                //LOGGER.warn("harshUpdate took longer than expected: {} ms", durationMs);
            }
        }
    }

    @Override
    public void reset() {
        long startTime = System.nanoTime();
        //LOGGER.info("Source points: {}", sourcePoints);
        //LOGGER.info("Endpoints: {}", endPoints);
        pathCache.clear();

        int pathsBuilt = 0;
        for (IResearchProvider provider : sourcePoints.values()) {
            for (IResearchReceiver receiver : endPoints.values()) {
                ResearchNetworkPath path = PathUtil.createPath(nodes, provider, receiver);
                //LOGGER.info("Path is: {}", path);
                if (path != null) {
                    pathCache.put(pair(provider.getPos(), receiver.getPos()), path);
                    pathsBuilt++;
                }
            }
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        //LOGGER.info("reset rebuilt {} paths in {} ms", pathsBuilt, durationMs);
    }


    @Override
    protected void transmit() {

    }

    public Map<BlockPos, IConnectableNode> getNodes() {
        return nodes;
    }
}
