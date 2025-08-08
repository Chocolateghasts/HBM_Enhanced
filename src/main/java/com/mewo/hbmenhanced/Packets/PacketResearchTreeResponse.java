package com.mewo.hbmenhanced.Packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class PacketResearchTreeResponse implements IMessage {
    private boolean success;
    private String message;

    public PacketResearchTreeResponse() {}

    public PacketResearchTreeResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        success = buf.readBoolean();
        message = readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(success);
        writeString(buf, message);
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

    public static class Handler implements IMessageHandler<PacketResearchTreeResponse, IMessage> {
        @Override
        public IMessage onMessage(PacketResearchTreeResponse message, MessageContext ctx) {
            // Client side — handle success/failure notification,
            // maybe show a chat message or GUI alert.
            if (ctx.side.isClient()) {
                if (!message.success) {
                    System.out.println("[Server] Failed: " + message.message);
                    // TODO: show player a chat message or GUI feedback
                } else {
                    System.out.println("[Server] Success: " + message.message);
                }
            }
            return null;
        }
    }
}

