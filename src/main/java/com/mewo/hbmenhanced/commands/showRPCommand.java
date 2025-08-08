package com.mewo.hbmenhanced.commands;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.util.Collections;
import java.util.List;

public class showRPCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "showRP";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/showRP - Shows the modid:name of the held item";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("This command can only be used by a player."));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        ItemStack heldItem = player.getHeldItem();

        if (heldItem == null) {
            player.addChatMessage(new ChatComponentText("You are not holding any item."));
            return;
        }

        Item item = heldItem.getItem();
        if (item == null) {
            player.addChatMessage(new ChatComponentText("Held item is invalid."));
            return;
        }

        String modItemId = GameRegistry.findUniqueIdentifierFor(item).toString();
        int meta = heldItem.getItemDamage();

        player.addChatMessage(new ChatComponentText("Held item: " + modItemId + " (meta: " + meta + ")"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        // You can restrict permission here if needed
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
