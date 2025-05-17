package com.mewo.hbmenhanced.structures;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Random;

public class MilitaryBase extends StructureStart {

    public MilitaryBase() {}

    public MilitaryBase(World world, Random rand, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);

        int x = (chunkX << 4) + 8;
        int z = (chunkZ << 4) + 8;
        int y = world.getTopSolidOrLiquidBlock(x, z);

        


    }

}
