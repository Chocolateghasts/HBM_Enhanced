package com.mewo.hbmenhanced.Connections.ResearchNetwork.Util;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IConnectableNode;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchProvider;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.IResearchReceiver;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.mewo.hbmenhanced.Connections.ResearchNetwork.ResearchNetwork.MAX_DEPTH;

public class PathUtil {
    private static final Logger LOGGER = LogManager.getLogger(PathUtil.class);
    public static ForgeDirection getDirectionBetween(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetX == dx && dir.offsetY == dy && dir.offsetZ == dz) {
                return dir;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    public static ResearchNetworkPath buildNetworkPath(Map<BlockPos, BlockPos> parentMap, BlockPos start, BlockPos end) {
        LinkedList<DirPos> result = new LinkedList<>();
        BlockPos current = end;

        while (!current.equals(start)) {
            BlockPos previous = parentMap.get(current);
            if (previous == null) {
                break;
            }

            ForgeDirection dir = getDirectionBetween(previous, current);
            result.addFirst(new DirPos(previous.getX(), previous.getY(), previous.getZ(), dir));
            current = previous;
        }

        return new ResearchNetworkPath(result);
    }

    public static ResearchNetworkPath createPath(Map<BlockPos, IConnectableNode> nodes, IResearchProvider provider, IResearchReceiver receiver) {
        LOGGER.info("Starting pathfinding from {} to {}", provider.getPos(), receiver.getPos());

        Set<BlockPos> marked = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> path = new HashMap<>();

        int checkedNodes = 0;
        BlockPos startPos = provider.getPos();
        BlockPos endPos = receiver.getPos();
        if (!nodes.containsKey(startPos) || !nodes.containsKey(endPos)) {
            LOGGER.info("Start or end node missing in nodes map");
            return null;
        }
        if (startPos.equals(endPos)) {
            LOGGER.info("Start and end positions are the same");
            return null;
        }

        queue.add(startPos);
        marked.add(startPos);
        while (!queue.isEmpty()) {
            if (marked.size() >= MAX_DEPTH) {
                LOGGER.info("Max pathfinding depth exceeded");
                return null;
            }
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
                        LOGGER.info("Path found with length {}", pathResult.length());
                        return pathResult;
                    }
                    queue.add(neighbor);
                }
            }
        }

        LOGGER.info("No path found from {} to {}", startPos, endPos);
        return null;
    }
}
