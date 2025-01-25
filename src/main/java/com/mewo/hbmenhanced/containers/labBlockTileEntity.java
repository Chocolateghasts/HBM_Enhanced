package com.mewo.hbmenhanced.containers;

import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class labBlockTileEntity extends TileEntity {
    private int customValue;

    public int getCustomValue() {
        return customValue;
    }

    public void setCustomValue(int value) {
        this.customValue = value;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("CustomValue", customValue);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.customValue = nbt.getInteger("CustomValue");
    }


}
