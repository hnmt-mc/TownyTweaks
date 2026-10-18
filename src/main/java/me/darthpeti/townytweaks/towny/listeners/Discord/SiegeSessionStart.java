package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.gmail.goosius.siegewar.events.BattleSessionStartedEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class SiegeSessionStart implements Listener {

    public SiegeSessionStart(Logger logger) {
    }

    @EventHandler
    public void onEvent(BattleSessionStartedEvent event) {
        Notifier.send("siegewar-session-start", Map.of(), new Color(214, 99, 84), null);
    }
}
