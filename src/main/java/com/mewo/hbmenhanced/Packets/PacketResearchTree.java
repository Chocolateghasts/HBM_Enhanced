package com.mewo.hbmenhanced.Packets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import com.mewo.hbmenhanced.ResearchBlocks.Util.ClientResearchSync;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PacketResearchTree implements IMessage {

    public enum SyncType {
        FULL_TREE,
        NODE_REPLACE,
        NODE_UNLOCK
    }

    private SyncType type;
    private String team;
    private Map<String, ResearchNode> fullTree;
    private String nodeId;
    private ResearchNode nodeData;
    private boolean unlocked;
    private int version = -1;

    public PacketResearchTree() {}

    public static PacketResearchTree fullTree(String team, Map<String, ResearchNode> tree, int version) {
        PacketResearchTree p = new PacketResearchTree();
        p.type = SyncType.FULL_TREE;
        p.team = team;
        p.fullTree = tree;
        p.version = version;
        return p;
    }

    public static PacketResearchTree nodeReplace(String id, ResearchNode node) {
        PacketResearchTree p = new PacketResearchTree();
        p.type = SyncType.NODE_REPLACE;
        p.nodeId = id;
        p.nodeData = node;
        return p;
    }

    public static PacketResearchTree nodeUnlock(String id, boolean unlocked) {
        PacketResearchTree p = new PacketResearchTree();
        p.type = SyncType.NODE_UNLOCK;
        p.nodeId = id;
        p.unlocked = unlocked;
        return p;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int typeOrdinal = buf.readByte();
        this.type = SyncType.values()[typeOrdinal];

        switch (type) {
            case FULL_TREE:
                int teamLen = buf.readInt();
                byte[] teamBytes = new byte[teamLen];
                buf.readBytes(teamBytes);
                this.team = new String(teamBytes, StandardCharsets.UTF_8);

                int jsonLen = buf.readInt();
                byte[] jsonBytes = new byte[jsonLen];
                buf.readBytes(jsonBytes);
                String json = new String(jsonBytes, StandardCharsets.UTF_8);
                Type treeType = new TypeToken<Map<String, ResearchNode>>() {}.getType();
                this.fullTree = new Gson().fromJson(json, treeType);

                this.version = buf.readInt();
                break;

            case NODE_REPLACE:
                this.nodeId = readString(buf);
                this.nodeData = new Gson().fromJson(readString(buf), ResearchNode.class);
                break;

            case NODE_UNLOCK:
                this.nodeId = readString(buf);
                this.unlocked = buf.readBoolean();
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());

        switch (type) {
            case FULL_TREE:
                writeString(buf, team);
                String json = new Gson().toJson(fullTree);
                writeString(buf, json);
                buf.writeInt(version);
                break;

            case NODE_REPLACE:
                writeString(buf, nodeId);
                writeString(buf, new Gson().toJson(nodeData));
                break;

            case NODE_UNLOCK:
                writeString(buf, nodeId);
                buf.writeBoolean(unlocked);
                break;
        }
    }

    private void writeString(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    private String readString(ByteBuf buf) {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static class Handler implements IMessageHandler<PacketResearchTree, IMessage> {
        @Override
        public IMessage onMessage(PacketResearchTree message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                switch (message.type) {
                    case FULL_TREE:
                        ClientResearchSync.fullPacket(message.team, message.fullTree, message.version);
                        break;
                    case NODE_REPLACE:
                        ClientResearchSync.replaceNode(message.nodeId, message.nodeData);
                        break;
                    case NODE_UNLOCK:
                        ClientResearchSync.changeUnlocked(message.nodeId, message.unlocked);
                        break;
                }
            } else {
                switch (message.type) {
                    case FULL_TREE:
                        break; // do nothing on server
                    case NODE_REPLACE:
                        break; // do nothing on server
                    case NODE_UNLOCK:
                        EntityPlayerMP plr = ctx.getServerHandler().playerEntity;
                        String team = plr.getEntityData().getString("hbmenhanced:team");
                        ResearchTree tree = ResearchTree.getTree(team);
                        System.out.println("[PKTSERVER] Getting tree for team: " + team);
                        System.out.println("[PKTSERVER] Getting tree: " + tree);
                        if (tree == null) {
                            return new PacketResearchTreeResponse(false, "Node and/or ResearchTree is null. Try to rejoin");
                        }
                        ResearchNode node = tree.getNode(message.nodeId);
                        if (node == null) {
                            return new PacketResearchTreeResponse(false, "Node and/or ResearchTree is null. Try to rejoin");
                        }
                        node.unlock(team, plr.getEntityWorld());
                        tree.save();
                        break;
                }
            }
            return null;
        }
    }
}
