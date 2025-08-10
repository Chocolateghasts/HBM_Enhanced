package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import com.mewo.hbmenhanced.Packets.PacketResearchTree;
import com.mewo.hbmenhanced.Packets.PacketResearchTreeResponse;
import com.mewo.hbmenhanced.Packets.PacketSyncTeam;
import com.mewo.hbmenhanced.recipes.ServerTemplates;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;

import static com.mewo.hbmenhanced.hbmenhanced.network;

public class ServerEventHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP mpPlayer = (EntityPlayerMP) event.player;
            String teamName = mpPlayer.getEntityData().getString("hbmenhanced:team");
            System.out.println("SERVER TEAM FOR PLR: " + teamName);
            network.sendTo(new PacketSyncTeam(teamName), mpPlayer);
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            String team = player.getEntityData().getString("hbmenhanced:team");
            ResearchTree tree = ResearchTree.getOrCreate(team);
            PacketResearchTree pkt = PacketResearchTree.fullTree(team, tree.nodes, tree.getVersion());
            network.sendTo(pkt, player);
            network.sendTo(ServerTemplates.fullSync(team), mpPlayer);
        }
    }
}
