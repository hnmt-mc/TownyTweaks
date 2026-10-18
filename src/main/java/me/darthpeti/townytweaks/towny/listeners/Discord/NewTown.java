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
        var town = event.getTown();
        var mayor = town.getMayor();
        Notifier.send("town-creation", Map.of("townName", town.getName(), "mayorName", mayor.getName(),
                "residents", String.valueOf(town.getNumResidents()), "nationName", Notifier.nationNameOrNone(town)),
                new Color(0, 81, 255), mayor.getUUID());
    }
}
