package com.mewo.hbmenhanced.Packets;

import com.mewo.hbmenhanced.ResearchBlocks.ResearchCable.TileEntityResearchCable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.Map;

public class ConnectionsPacket implements IMessage {
    private int x, y, z;
    private Map<ForgeDirection, Boolean> connections = new EnumMap<>(ForgeDirection.class);

    public ConnectionsPacket() {} // Required

    public ConnectionsPacket(int x, int y, int z, Map<ForgeDirection, Boolean> connections) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.connections.putAll(connections);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            buf.writeBoolean(connections.getOrDefault(dir, false));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            boolean connected = buf.readBoolean();
            connections.put(dir, connected);
        }
    }

    public static class Handler implements IMessageHandler<ConnectionsPacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ConnectionsPacket message, MessageContext ctx) {
            EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().thePlayer;
            World world = player.worldObj;

            TileEntity te = world.getTileEntity(message.x, message.y, message.z);
            if (te instanceof TileEntityResearchCable) {
                ((TileEntityResearchCable) te).updateConnectionsClient(message.connections);
                world.markBlockRangeForRenderUpdate(message.x, message.y, message.z, message.x, message.y, message.z);
            }

            return null;
        }
    }
}
