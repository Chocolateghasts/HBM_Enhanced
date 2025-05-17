package com.mewo.hbmenhanced.structures;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

public class TestStructure extends MapGenStructure {

    private double spawnChance = 0.01D;

    public TestStructure() {}

    @Override
    public String func_143025_a() {
        return "Structure_0";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return this.rand.nextDouble() < spawnChance;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new MilitaryBase();
    }
}
