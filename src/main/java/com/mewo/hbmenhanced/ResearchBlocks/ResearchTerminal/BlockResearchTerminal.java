package com.mewo.hbmenhanced.ResearchBlocks.ResearchTerminal;

import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockResearchTerminal extends BlockContainer {
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBack;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconWest;

    protected BlockResearchTerminal() {
        super(Material.anvil);
        setCreativeTab(hbmenhanced.tabhbmenhanced);
        setHardness(4);
        setResistance(4);
        this.setBlockName("ResearchTerminal");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityResearchTerminal();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(hbmenhanced.instance, hbmenhanced.guiResearchTerminalID, world, x, y, z);
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(int metaData) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(hbmenhanced.MODID + ":" + "/terminal/sideb");
        this.iconFront = register.registerIcon(hbmenhanced.MODID + ":" + "/terminal/sideb");
        this.iconBack = register.registerIcon(hbmenhanced.MODID + ":" + "/terminal/back");
        this.iconTop = register.registerIcon(hbmenhanced.MODID + ":" + "/terminal/top");
        this.iconBottom = register.registerIcon(hbmenhanced.MODID + ":" + "/terminal/bottom");
        this.iconWest = register.registerIcon(hbmenhanced.MODID + ":" + "/terminal/sidea");
    }
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        switch (side) {
            case 0: // Bottom
                return this.iconBottom;
            case 1: // Top
                return this.iconTop;
            case 2: // North
                return this.iconFront;
            case 3: // South
                return this.iconBack;
            case 4: // West
                return this.iconWest;
            case 5: // East
                return this.iconWest;
            default:
                return this.blockIcon;
        }
    }


}
