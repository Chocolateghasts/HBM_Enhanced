package com.mewo.hbmenhanced.Packets;

import com.mewo.hbmenhanced.Util.ResearchTemplate;
import com.mewo.hbmenhanced.recipes.ClientTemplateSync;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.*;

public class PacketTemplates implements IMessage {
    public enum PacketType {
        FULL_SYNC, UPDATE;
    }

    private PacketType type;
    private int version;
    private Set<ResearchTemplate> templates = new HashSet<>();

    public PacketTemplates() {}

    public PacketTemplates(PacketType type, int version, Set<ResearchTemplate> templates) {
        this.type = type;
        this.version = version;
        this.templates = templates;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeInt(version);
        buf.writeInt(templates.size());
        for (ResearchTemplate template : templates) {
            ByteBufUtils.writeUTF8String(buf, template.getType());
            ByteBufUtils.writeUTF8String(buf, template.getId());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int ordinal = buf.readByte();
        this.type = PacketType.values()[ordinal];
        this.version = buf.readInt();
        int size = buf.readInt();
        this.templates = new HashSet<>();
        for (int i = 0; i < size; i++) {
            String type = ByteBufUtils.readUTF8String(buf);
            String id = ByteBufUtils.readUTF8String(buf);
            this.templates.add(new ResearchTemplate(type, id));
        }
    }

    public PacketType getType() { return type; }
    public int getVersion() { return version; }
    public Set<ResearchTemplate> getTemplates() { return templates; }

    public static class Handler implements IMessageHandler<PacketTemplates, IMessage> {
        @Override
        public IMessage onMessage(final PacketTemplates message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                if (message.getType() == PacketType.FULL_SYNC) {
                    if (message.getVersion() > ClientTemplateSync.version) {
                        ClientTemplateSync.version = message.getVersion();
                        ClientTemplateSync.templates.clear();
                        ClientTemplateSync.templates.addAll(message.getTemplates());
                    }
                } else if (message.getType() == PacketType.UPDATE) {
                    if (message.getVersion() > ClientTemplateSync.version) {
                        ClientTemplateSync.version = message.getVersion();
                        ClientTemplateSync.templates.addAll(message.getTemplates());
                    }
                }
            }
            return null;
        }
    }
}
