package com.mewo.hbmenhanced.items;

import com.mewo.hbmenhanced.getRpValue;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.HashMap;

import static com.mewo.hbmenhanced.hbmenhanced.tabhbmenhanced;

public class ItemResearchPoint extends Item {

    public ItemResearchPoint() {
        this.setCreativeTab(tabhbmenhanced);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();
        for (getRpValue.researchType rType : getRpValue.researchType.values()) {
            nbt.setInteger(rType.toString(), 0);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(hbmenhanced.MODID + ":" + this.getUnlocalizedName().substring(5));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            NBTTagCompound playerNbt = player.getEntityData();
            NBTTagCompound nbt = itemStack.getTagCompound();
            if (!playerNbt.hasKey("hbmenhanced:team")) {
                player.addChatMessage(new ChatComponentText("You must be in a team to research items!"));
                return itemStack;
            }
            String team = playerNbt.getString("hbmenhanced:team");
            if (team.isEmpty()) {
                player.addChatMessage(new ChatComponentText("You must be in a team to research items!"));
                return itemStack;
            }
            for (getRpValue.researchType type : getRpValue.researchType.values()) {
                int points = getRp(itemStack, type.toString());
                if (points > 0) {
                    getRpValue.addResearchPoints(team, type, points);
                    HashMap<String, EnumMap<getRpValue.researchType, Integer>> teamValues = getRpValue.getTeamRpMap();
                    System.out.println(teamValues);

                }
            }
        }
        --itemStack.stackSize;
        return itemStack;
    }

    public static void setRp(ItemStack itemStack, String type, int points) {
        if (!itemStack.hasTagCompound()) {
            System.out.println("No nbt");
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = itemStack.getTagCompound();
        for (getRpValue.researchType rType : getRpValue.researchType.values()) {
            if (!nbt.hasKey(rType.toString())) {
                nbt.setInteger(rType.toString(), 0);
            }
        }
        nbt.setInteger(type, points);
        itemStack.setTagCompound(nbt);
    }

    public int getRp(ItemStack itemStack, String type) {
        if (!itemStack.hasTagCompound()) {
            System.out.println("No nbt");
            itemStack.setTagCompound(new NBTTagCompound());
        }
        return itemStack.getTagCompound().getInteger(type);
    }
}