package com.mewo.hbmenhanced.Structures.MilitaryBase;

import com.mewo.hbmenhanced.Structures.SchematicLoader;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class StructureMilitaryBase extends StructureComponent {

    public StructureMilitaryBase() {}

    public StructureMilitaryBase(Random rand, int x, int z) {
        this.boundingBox = new StructureBoundingBox(x, 0, z, x + 10, 5 + 10, z + 10);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbtTagCompound) {

    }

    @Override
    protected void func_143011_b(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        int y = world.getTopSolidOrLiquidBlock(this.boundingBox.minX, this.boundingBox.minZ);
        this.boundingBox.minY = y;
        this.boundingBox.maxY = y + 5;

        InputStream stream = getClass().getResourceAsStream("/assets/hbmenhanced/structures/Lambda_Core/Lamda_Core_Entrance.schematic");
        if (stream == null) {
            System.out.println("Could not find schematic");
            return false;
        }
        try {
            NBTTagCompound schematic = CompressedStreamTools.readCompressed(stream);
            SchematicLoader.spawnSchematic(world, this.boundingBox.minX, this.boundingBox.maxY, this.boundingBox.minZ, schematic, "Lambda_Core");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }
}

/*
try {
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(
                    new ResourceLocation("hbmenhanced", "structures/military_base.schematic")
            ).getInputStream();

            SchematicLoader.spawnSchematic(world, this.boundingBox.minX, this.boundingBox.maxY, this.boundingBox.minZ, stream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
 */