package com.mewo.hbmenhanced.Structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.IOException;
import java.io.InputStream;

public class SchematicLoader {

    public static void spawnSchematic(World world, int xStart, int yStart, int zStart, InputStream stream) throws IOException {
        NBTTagCompound schematic = CompressedStreamTools.readCompressed(stream);

        int height = schematic.getInteger("Height");
        int width = schematic.getInteger("Width");
        int length = schematic.getInteger("Length");

        byte[] blocks = schematic.getByteArray("Blocks");
        byte[] data = schematic.getByteArray("Data");

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width * length + z * width + x;

                    int blockId = blocks[index] & 0xFF;
                    int meta = data[index] & 0xFF;

                    Block block = Block.getBlockById(blockId);
                    if (block != null && block != Blocks.air) {
                        world.setBlock(xStart + x, yStart + y, zStart + z, block, meta, 2);
                    }
                }
            }
        }

    }

}
