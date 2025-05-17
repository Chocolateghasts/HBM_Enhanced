package com.mewo.hbmenhanced.structures;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import java.util.Random;

public class MilitaryBaseComponent extends StructureComponent {

    public MilitaryBaseComponent(Random rand, int x, int y, int z, int scaleX, int scaleY, int scaleZ) {
        this.boundingBox = new StructureBoundingBox(x, y, z, x + scaleX, y + scaleY, z + scaleZ);
    }

    @Override
    protected void func_143012_a(NBTTagCompound p_143012_1_) {

    }

    @Override
    protected void func_143011_b(NBTTagCompound p_143011_1_) {

    }

    @Override
    public boolean addComponentParts(World p_74875_1_, Random p_74875_2_, StructureBoundingBox p_74875_3_) {
        return false;
    }
}
