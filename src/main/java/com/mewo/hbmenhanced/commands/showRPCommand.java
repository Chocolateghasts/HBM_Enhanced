package com.mewo.hbmenhanced.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.Collections;
import java.util.List;

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
        String playerName = sender.getCommandSenderName();



        sender.addChatMessage(new ChatComponentText(playerName + "'s RP is " + RPCommand.ResearchPoints));
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
