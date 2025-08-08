package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.hbm.util.fauxpointtwelve.BlockPos;

import java.util.*;

public abstract class AbstractNetwork<TNode extends IConnectableNode> {
    protected Map<BlockPos, TNode> nodes = new HashMap<>();
    protected Queue<TNode> changedNodes = new LinkedList<>();
    protected Set<BlockPos> changedPositions = new HashSet<>();

    protected int tickCounter = 0;
    protected boolean needsReset = false;

    public void update() {
        tickCounter++;
        if (tickCounter >= 1_000_000) tickCounter = 0;

        if (needsReset) {
            reset();
            needsReset = false;
        } else if (tickCounter % 60 == 0) {
            harshUpdate();
        }

        transmit();
    }

    public void add(TNode node) {
        TNode oldNode = nodes.get(node.getPos());
        if (oldNode != null) queueNode(oldNode);

        nodes.put(node.getPos(), node);
        queueNode(node);

        onAdd(node);

        needsReset = true;
    }

    public void remove(TNode node) {
        TNode oldNode = nodes.get(node.getPos());
        queueNode(oldNode);

        nodes.remove(node.getPos());
        onRemove(node);

        needsReset = true;
    }

    protected abstract void onAdd(TNode node);
    protected abstract void onRemove(TNode node);

    public void queueNode(TNode node) {
        if (node == null) return;
        if (!changedPositions.add(node.getPos())) return;

        changedNodes.add(node);
    }

    // Abstract or default implementations
    protected abstract void harshUpdate();
    public abstract void reset();
    protected abstract void transmit();

}
