package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.palmergames.bukkit.towny.event.NewNationEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class NewNation implements Listener {

    public NewNation(Logger logger) {
    }

    @EventHandler
    public void onEvent(NewNationEvent event) {
        var king = event.getNation().getKing();
        Notifier.send("nation-create", Map.of("nationName", event.getNation().getName(), "kingName", king.getName()),
                new Color(0, 81, 255), king.getUUID());
    }
}
