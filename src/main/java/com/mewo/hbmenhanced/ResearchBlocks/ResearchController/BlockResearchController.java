package com.mewo.hbmenhanced.ResearchBlocks.ResearchController;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.TileEntityT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockResearchController extends BlockContainer {

    // TODO: Add actual usefulness to this block

    public BlockResearchController() {
        super(Material.anvil);
        setCreativeTab(hbmenhanced.tabhbmenhanced);
        setHardness(4);
        setResistance(4);
        setBlockName("ResearchController");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityResearchController();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(hbmenhanced.instance, hbmenhanced.guiResearchControllerID, world, x, y, z);
        }
        return true;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileEntityResearchController)) return;
        TileEntityResearchController controller = (TileEntityResearchController) tileEntity;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    TileEntity neighborTE = world.getTileEntity(x + dx, y + dy, z + dz);
                    if (neighborTE != null && controller.isResearchBlock(neighborTE)) {
                        controller.addConnection(neighborTE);
                    }
                }
            }
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess worldAccess, int x, int y, int z, int neighborX, int neighborY, int neighborZ) {
        if (!(worldAccess instanceof World)) {
            System.out.println("Not a World instance :(");
            return;
        }

        World world = (World) worldAccess;
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (!(tileEntity instanceof TileEntityResearchController)) {
            return;
        }

        TileEntityResearchController controller = (TileEntityResearchController) tileEntity;
        TileEntity neighborTE = world.getTileEntity(neighborX, neighborY, neighborZ);

        if (neighborTE != null && controller.isResearchBlock(neighborTE)) {
            controller.addConnection(neighborTE);
        } else {
            for (TileEntity connectedTE : controller.connectedPos.keySet()) {
                BlockPos pos = controller.connectedPos.get(connectedTE);
                if (pos.getX() == neighborX && pos.getY() == neighborY && pos.getZ() == neighborZ) {
                    controller.removeConnection(connectedTE);
                    break;
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileEntityResearchController)) {
            super.breakBlock(world, x, y, z, block, meta);
            return;
        }

        TileEntityResearchController controller = (TileEntityResearchController) tileEntity;

        List<TileEntity> toRemove = new ArrayList<>(controller.connectedPos.keySet());
        for (TileEntity connectedTE : toRemove) {
            controller.removeConnection(connectedTE);
        }

        super.breakBlock(world, x, y, z, block, meta);
    }
}