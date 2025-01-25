package com.mewo.hbmenhanced.commands;

import ibxm.Player;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import scala.Int;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RPCommand implements ICommand {
    int points = 0;
    public static int ResearchPoints = 0;
    public static HashMap<String, Integer> playerRPMap = new HashMap<>();

    @Override
    public String getCommandName() {
        return "rp";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/rp <set/add/subtract> <player> <points>";
    }

    @Override
    public List getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        String action = args[0];
        String playerName = args[1];

        int currentRP = playerRPMap.getOrDefault(playerName, 0);



        //playerName = args[1];
        System.out.println(playerName);
        points = Integer.parseInt(args[2]);

        if (action != null && action.equals("set")) {
            sender.addChatMessage(new ChatComponentText("Set " + playerName + "'s RP to " + points));
            playerRPMap.put(playerName, points);
        } else if (action != null && action.equals("add")) {
            sender.addChatMessage(new ChatComponentText("Add " + points + " to " + playerName));
            playerRPMap.put(playerName, currentRP + points);
        } else if (action != null && action.equals("subtract")) {
            if (currentRP - points < 0) {
                sender.addChatMessage(new ChatComponentText(playerName + "'s RP cannot go below zero."));
            } else {
                // Subtract points from the player's RP
                playerRPMap.put(playerName, currentRP - points);
                sender.addChatMessage(new ChatComponentText("Subtracted " + points + " from " + playerName + "'s RP"));
            }
        }
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
        return i == 1;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
