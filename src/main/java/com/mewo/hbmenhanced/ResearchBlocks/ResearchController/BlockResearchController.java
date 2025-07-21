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

public class BlockResearchController extends BlockContainer {

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
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        if (!(world instanceof World)) {
            System.out.println("not world :("); return;
        }
        TileEntity tileEntity = world.getTileEntity(tileX, tileY, tileZ);
        TileEntity te = world.getTileEntity(x, y, z);
        TileEntityResearchController controller;

        if (te instanceof TileEntityResearchController) {
            controller = (TileEntityResearchController) te;
        }

        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityT1) {

        }
        if (tileEntity instanceof TileEntityT2) {
        }
        if (tileEntity instanceof TileEntityT3) {
        }

    }
}