package com.mewo.hbmenhanced.Packets;

import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.nio.charset.StandardCharsets;

public class PacketResearchTreeRequest implements IMessage {

    public enum RequestType {
        FULL_TREE,
        NODE,
        VERSION_CHECK
    }

    private RequestType requestType;
    private String team;
    private String nodeId;      // only for NODE request
    private int clientVersion;  // only for VERSION_CHECK request

    // Required empty constructor
    public PacketResearchTreeRequest() {}

    // Constructors for convenience
    public static PacketResearchTreeRequest fullTree(String team) {
        PacketResearchTreeRequest p = new PacketResearchTreeRequest();
        p.requestType = RequestType.FULL_TREE;
        p.team = team;
        return p;
    }

    public static PacketResearchTreeRequest node(String team, String nodeId) {
        PacketResearchTreeRequest p = new PacketResearchTreeRequest();
        p.requestType = RequestType.NODE;
        p.team = team;
        p.nodeId = nodeId;
        return p;
    }

    public static PacketResearchTreeRequest versionCheck(String team, int clientVersion) {
        PacketResearchTreeRequest p = new PacketResearchTreeRequest();
        p.requestType = RequestType.VERSION_CHECK;
        p.team = team;
        p.clientVersion = clientVersion;
        return p;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int typeOrdinal = buf.readByte();
        this.requestType = RequestType.values()[typeOrdinal];
        this.team = readString(buf);

        switch (requestType) {
            case NODE:
                this.nodeId = readString(buf);
                break;
            case VERSION_CHECK:
                this.clientVersion = buf.readInt();
                break;
            case FULL_TREE:
                // no additional data
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(requestType.ordinal());
        writeString(buf, team);

        switch (requestType) {
            case NODE:
                writeString(buf, nodeId);
                break;
            case VERSION_CHECK:
                buf.writeInt(clientVersion);
                break;
            case FULL_TREE:
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

    public RequestType getRequestType() { return requestType; }
    public String getTeam() { return team; }
    public String getNodeId() { return nodeId; }
    public int getClientVersion() { return clientVersion; }

    public static class Handler implements IMessageHandler<PacketResearchTreeRequest, IMessage> {
        @Override
        public IMessage onMessage(PacketResearchTreeRequest message, MessageContext ctx) {
            // Only handle on server side!
            if (!ctx.side.isServer()) {
                return null;
            }

            // Get player who sent the request
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            ResearchTree tree = ResearchTree.getTree(message.getTeam());
            if (tree == null) {
                // Send failure response to player
                hbmenhanced.network.sendTo(
                        new PacketResearchTreeResponse(false, "Team not found: " + message.getTeam()), player);
                return null;
            }

            switch (message.getRequestType()) {
                case FULL_TREE: {
                    PacketResearchTree fullPacket = PacketResearchTree.fullTree(message.getTeam(), tree.nodes, tree.getVersion());
                    // Optionally set version if you have versioning
                    // fullPacket.setVersion(tree.getVersion());
                    hbmenhanced.network.sendTo(fullPacket, player);
                    break;
                }
                case NODE: {
                    ResearchNode node = tree.getNode(message.getNodeId());
                    if (node == null) {
                        hbmenhanced.network.sendTo(
                                new PacketResearchTreeResponse(false, "Node not found: " + message.getNodeId()), player);
                        break;
                    }
                    PacketResearchTree nodePacket = PacketResearchTree.nodeReplace(message.getNodeId(), node);
                    hbmenhanced.network.sendTo(nodePacket, player);
                    break;
                }
                case VERSION_CHECK: {
                    int serverVersion = tree.getVersion();
                    if (serverVersion != message.getClientVersion()) {
                        System.out.println("Sending full packet of nodes: " + tree.nodes);
                        if (tree.getNode("basic_power") != null) {
                            System.out.println(tree.getNode("basic_power").isUnlocked);
                            System.out.println("Team is: " + message.getTeam());
                        }
                        PacketResearchTree fullPacket = PacketResearchTree.fullTree(message.getTeam(), tree.nodes, serverVersion);
                        hbmenhanced.network.sendTo(fullPacket, player);
                    }
                    break;
                }
            }

            return null;
        }
    }
}
