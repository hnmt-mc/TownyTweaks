package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.palmergames.bukkit.towny.event.NewTownEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class NewTown implements Listener {

    public NewTown(Logger logger) {
    }

    @EventHandler
    public void onEvent(NewTownEvent event) {
        var mayor = event.getTown().getMayor();
        Notifier.send("town-creation", Map.of("townName", event.getTown().getName(), "mayorName", mayor.getName()),
                new Color(0, 81, 255), mayor.getUUID());
    }
}
