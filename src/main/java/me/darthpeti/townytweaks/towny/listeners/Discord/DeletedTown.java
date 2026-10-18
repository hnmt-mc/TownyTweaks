package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class DeletedTown implements Listener {

    public DeletedTown(Logger logger) {
    }

    @EventHandler
    public void onEvent(DeleteTownEvent event) {
        var mayor = event.getMayor();
        Notifier.send("town-delete", Map.of("townName", event.getTownName(), "mayorName", mayor == null ? "" : mayor.getName()),
                new Color(214, 99, 84), event.getMayorUUID());
    }
}
