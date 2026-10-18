package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.palmergames.bukkit.towny.event.DeleteNationEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class DeletedNation implements Listener {

    public DeletedNation(Logger logger) {
    }

    @EventHandler
    public void onEvent(DeleteNationEvent event) {
        var leader = event.getLeader();
        Notifier.send("nation-disband", Map.of("nationName", event.getNationName(), "kingName", leader == null ? "" : leader.getName()),
                new Color(214, 99, 84), event.getLeaderUUID());
    }
}
