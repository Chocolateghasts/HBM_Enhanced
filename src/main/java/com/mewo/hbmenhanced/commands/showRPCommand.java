package com.mewo.hbmenhanced.commands;

import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
//import li.cil.oc.

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mewo.hbmenhanced.commands.RPCommand.playerRPMap;

public class showRPCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "showRP";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/showRP <player>";
    }

    @Override
    public List getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        System.out.println(playerRPMap);
        StringBuilder rpList = new StringBuilder("Current RP values:\n");
        // Iterate over the map and format each entry
        for (Map.Entry<String, Integer> entry : playerRPMap.entrySet()) {
            rpList.append(entry.getKey())
                    .append(" has ")
                    .append(entry.getValue())
                    .append(" RP\n");
        }
        sender.addChatMessage(new ChatComponentText(rpList.toString()));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
