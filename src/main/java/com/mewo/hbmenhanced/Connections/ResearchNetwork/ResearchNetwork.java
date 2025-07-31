package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.Util.PathUtil;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.Util.ResearchNetworkPath;
import com.hbm.util.fauxpointtwelve.DirPos;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ResearchNetwork {
    public static final int MAX_DEPTH = 500;
    private static final Logger LOGGER = LogManager.getLogger(ResearchNetwork.class);

    private int tickCounter;

    public Map<BlockPos, IConnectableNode> nodes = new HashMap<>();
    public Map<BlockPos, IResearchReceiver> endPoints = new HashMap<>();
    public Map<BlockPos, IResearchProvider> sourcePoints = new HashMap<>();

    public Queue<IConnectableNode> changedNodes = new LinkedList<>();
    private Set<BlockPos> changedPositions = new HashSet<>();

    public Map<Pair<BlockPos, BlockPos>, ResearchNetworkPath> pathCache = new HashMap<>();
    private boolean needsReset = false;

    private <L, R> Pair<L, R> pair(L left, R right) {
        return Pair.of(left, right);
    }

    public void harshUpdate() {
        LOGGER.info("Path cache size: {}", pathCache.size());

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
            LOGGER.info("harshUpdate processed {} changed nodes, rebuilt {} paths in {} ms", changedNodeCount, pathsRebuilt, durationMs);
            if (durationMs > 50) {
                LOGGER.warn("harshUpdate took longer than expected: {} ms", durationMs);
            }
        }
    }

    public void update() {
        tickCounter++;
        if (tickCounter >= 1_000_000) {
            tickCounter = 0;
        }
        if (needsReset) {
            reset();
            needsReset = false;
        } else if (tickCounter % 60 == 0) {
            harshUpdate();
        }
        transmit();
    }

    public boolean isConnected(IResearchProvider provider, IResearchReceiver receiver) {
        return pathCache.containsKey(pair(provider.getPos(), receiver.getPos()));
    }

    public void reset() {
        long startTime = System.nanoTime();
        LOGGER.info("Source points: {}", sourcePoints);
        LOGGER.info("Endpoints: {}", endPoints);
        pathCache.clear();

        int pathsBuilt = 0;
        for (IResearchProvider provider : sourcePoints.values()) {
            for (IResearchReceiver receiver : endPoints.values()) {
                ResearchNetworkPath path = PathUtil.createPath(nodes, provider, receiver);
                LOGGER.info("Path is: {}", path);
                if (path != null) {
                    pathCache.put(pair(provider.getPos(), receiver.getPos()), path);
                    pathsBuilt++;
                }
            }
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        LOGGER.info("reset rebuilt {} paths in {} ms", pathsBuilt, durationMs);
    }

    public void transmit() {

    }

    private void queueNode(IConnectableNode node) {
        if (node == null) return;
        if (!changedPositions.add(node.getPos())) return;
        LOGGER.info("Queing node: {}", node);
        LOGGER.info("Queing node: {}", node);
        changedNodes.add(node);
    }

    public void add(IConnectableNode node) {
        LOGGER.info("Adding node at position {}", node.getPos());
        IConnectableNode oldNode = nodes.get(node.getPos());
        if (oldNode != null) queueNode(oldNode);

        nodes.put(node.getPos(), node);
        queueNode(node);

        if (node instanceof IResearchProvider) {
            IResearchProvider provider = ((IResearchProvider) node);
            sourcePoints.put(provider.getPos(), provider);
        }
        if (node instanceof IResearchReceiver) {
            IResearchReceiver receiver = ((IResearchReceiver) node);
            endPoints.put(receiver.getPos(), receiver);
        }
        needsReset = true;
    }

    public void remove(IConnectableNode node) {
        LOGGER.info("Removing node at position {}", node.getPos());
        IConnectableNode oldNode = nodes.get(node.getPos());
        queueNode(oldNode);

        nodes.remove(node.getPos());
        sourcePoints.remove(node.getPos());
        endPoints.remove(node.getPos());
        needsReset = true;
    }


    public Map<BlockPos, IConnectableNode> getNodes() {
        return nodes;
    }

}
