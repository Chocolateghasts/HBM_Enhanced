package com.mewo.hbmenhanced.Packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.nio.charset.StandardCharsets;

public class PacketSyncTeam implements IMessage {
    private String team;

    public PacketSyncTeam() {}
    public PacketSyncTeam(String team) { this.team = team; }

    @Override
    public void fromBytes(ByteBuf buf) {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        team = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = team.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static class Handler implements IMessageHandler<PacketSyncTeam, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncTeam message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().thePlayer;
                player.getEntityData().setString("hbmenhanced:team", message.team);
            }
            return null;
        }
    }
}

