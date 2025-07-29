package com.mewo.hbmenhanced.Connections.ResearchNetwork.Util;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PathUtil {
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


}
