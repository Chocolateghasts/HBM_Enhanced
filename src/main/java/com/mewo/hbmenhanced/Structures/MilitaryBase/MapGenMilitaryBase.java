package com.mewo.hbmenhanced.Structures.MilitaryBase;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Random;

public class MapGenMilitaryBase extends MapGenStructure{
    public int scale = 1;

    @Override
    public String func_143025_a() {
        return "MilitaryBase";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        double chance = 0.005 * scale;
        int minDistFromWorldOrigin = 32;

        int distance = Math.max(Math.abs(chunkX), Math.abs(chunkZ));
        if (distance < minDistFromWorldOrigin) {return false;}
        Random rand = this.worldObj.setRandomSeed(chunkX, chunkZ, 10387312);
        if (rand.nextDouble() < chance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected StructureStart getStructureStart(int ChunkX, int ChunkZ) {
        Random rand = this.worldObj.setRandomSeed(ChunkX, ChunkZ, 10387312);
        return new StructureStartMilitaryBase(this.worldObj, rand, ChunkX, ChunkZ);
    }
}
