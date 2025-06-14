package com.mewo.hbmenhanced.Structures.MilitaryBase;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class StructureStartMilitaryBase extends StructureStart {

    public StructureStartMilitaryBase() {}

    public StructureStartMilitaryBase(World world, Random rand, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);

        StructureMilitaryBase component = new StructureMilitaryBase(rand, chunkX * 16, chunkZ * 16);
        this.components.add(component);
        this.updateBoundingBox();
    }

}
