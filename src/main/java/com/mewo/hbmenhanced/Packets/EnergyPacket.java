package com.mewo.hbmenhanced.Packets;

import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

public class EnergyPacket implements IMessage {

    private int x, y, z;
    private int energy;

    public EnergyPacket() {}

    public EnergyPacket(TileEntityResearchCore te) {
        this.x = te.xCoord;
        this.y = te.yCoord;
        this.z = te.zCoord;
        this.energy = te.getEnergyStored(null);
    }



    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.energy = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(energy);
    }

    public static class Handler implements IMessageHandler<EnergyPacket, IMessage> {
        @Override
        public IMessage onMessage(EnergyPacket message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(
                        message.x, message.y, message.z);
                if (te instanceof TileEntityResearchCore) {
                    ((TileEntityResearchCore) te).setClientEnergy(message.energy);
                }
            }
            return null;
        }
    }
}
