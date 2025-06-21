package com.mewo.hbmenhanced.Packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class ResearchTier1Packet implements IMessage {

    private int x, y, z;
    private int currentBurnTime, researchProgress, maxResearchProgress;
    private boolean isResearching;
    private String team;

    public ResearchTier1Packet() {}

    public ResearchTier1Packet(int x, int y, int z, int currentBurnTime, int researchProgress, int maxResearchProgress, boolean isResearching) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.currentBurnTime = currentBurnTime;
        this.researchProgress = researchProgress;
        this.maxResearchProgress = maxResearchProgress;
        this.isResearching = isResearching;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.currentBurnTime = buf.readInt();
        this.researchProgress = buf.readInt();
        this.maxResearchProgress = buf.readInt();
        this.isResearching = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(currentBurnTime);
        buf.writeInt(researchProgress);
        buf.writeInt(maxResearchProgress);
        buf.writeBoolean(isResearching);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getCurrentBurnTime() { return currentBurnTime; }
    public int getResearchProgress() { return researchProgress; }
    public int getMaxResearchProgress() { return maxResearchProgress; }
    public boolean isResearching() { return isResearching; }

}
