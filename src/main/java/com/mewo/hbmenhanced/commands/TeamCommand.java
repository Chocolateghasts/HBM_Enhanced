package com.mewo.hbmenhanced.commands;

import com.mewo.hbmenhanced.TeamData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class TeamCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "team";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.team.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("commands.team.usage", new Object[0]);
        }

        EntityPlayerMP targetPlayer = getPlayer(sender, args[1]);

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                throw new WrongUsageException("commands.team.set.usage", new Object[0]);
            }
            TeamData.setTeam(targetPlayer, args[2]);
            func_152373_a(sender, this, "Team set to: " + args[2]);
        } else if (args[0].equalsIgnoreCase("get")) {
            String team = TeamData.getTeam(targetPlayer);
            func_152373_a(sender, this, targetPlayer.getCommandSenderName() + "'s team is: " + team);
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, new String[]{"set", "get"});
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, this.getPlayers());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return getListOfStringsMatchingLastWord(args, new String[]{"Stalin"});
        }
        return null;
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
