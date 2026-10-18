package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class RuinedTown implements Listener {

    public RuinedTown(Logger logger) {
    }

    @EventHandler
    public void onEvent(TownRuinedEvent event) {
        var town = event.getTown();
        String oldMayor = event.getOldMayorName();
        Notifier.send("town-ruins", Map.of("townName", town.getName(), "mayorName", oldMayor == null ? "" : oldMayor,
                "residents", String.valueOf(town.getNumResidents()), "blocks", String.valueOf(town.getNumTownBlocks()),
                "nationName", Notifier.nationNameOrNone(town)), new Color(214, 99, 84), null);
    }
}
