package com.mewo.hbmenhanced.Structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;

public class SchematicLoader {

    Random random = new Random();

    public NBTTagCompound getNBT(File schematicFile) {
        try {
            return CompressedStreamTools.readCompressed(new FileInputStream(schematicFile));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getHallways(String structure, World world) {
        ISaveHandler saveHandler = world.getSaveHandler();
        File worldDir = ((SaveHandler) saveHandler).getWorldDirectory();
        File target = new File(worldDir, "structures/" + structure + "/hallways");
        File[] files = target.listFiles();
        List<String> temp = new ArrayList<>();
        for (File file : files) {
            temp.add(file.getName());
        }
        return temp;
    }

    public void spawnNextHallway(World world, int x, int y, int z, String structure) {
        List<String> hallways = getHallways(structure, world);
        String hallway = hallways.get(random.nextInt(hallways.size()));
        ISaveHandler saveHandler = world.getSaveHandler();
        File worldDir = ((SaveHandler) saveHandler).getWorldDirectory();
        File hall = new File(worldDir, "structures/" + structure + "/hallways/" + hallway);
        NBTTagCompound schematic = getNBT(hall);
        try {
            spawnSchematic(world, x, y, z, schematic, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void spawnSchematic(World world, int xStart, int yStart, int zStart, NBTTagCompound schematic, String structure) throws IOException {


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
