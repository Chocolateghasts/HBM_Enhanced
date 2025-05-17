package com.mewo.hbmenhanced;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class TeamData {
    public static void setTeam(EntityPlayerMP player, String team) {
        NBTTagCompound nbt = player.getEntityData();

        if (nbt.hasKey("hbmenhanced:team")) {
            System.out.println("Already in a team");
            nbt.setString("hbmenhanced:team", team);
        } else {
            nbt.setString("hbmenhanced:team", team);
        }
    }
    public static String getTeam(EntityPlayerMP player) {
        NBTTagCompound nbt = player.getEntityData();
        if (!nbt.hasKey("hbmenhanced:team")) {
            System.out.println("Not in a team");
            return "Independent";
        } else {
            return nbt.getString("hbmenhanced:team");
        }
    }
}
