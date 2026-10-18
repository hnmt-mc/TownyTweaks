package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.gmail.goosius.siegewar.events.BattleSessionEndedEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class SiegeSessionEnd implements Listener {

    public SiegeSessionEnd(Logger logger) {
    }

    @EventHandler
    public void onEvent(BattleSessionEndedEvent event) {
        Notifier.send("siegewar-session-end", Map.of(), new Color(214, 99, 84), null);
    }
}
