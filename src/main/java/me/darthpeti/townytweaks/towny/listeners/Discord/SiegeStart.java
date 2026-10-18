package me.darthpeti.townytweaks.towny.listeners.Discord;

import com.gmail.goosius.siegewar.events.SiegeWarStartEvent;
import me.darthpeti.townytweaks.towny.util.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class SiegeStart implements Listener {

    public SiegeStart(Logger logger) {
    }

    @EventHandler
    public void onEvent(SiegeWarStartEvent event) {
        var target = event.getTargetTown();
        String defender = target.hasNation() ? target.getNationOrNull().getName() : target.getName();
        Notifier.send("siegewar-siege-start", Map.of("attacker", event.getTownOfSiegeStarter().getName(), "defender", defender,
                "townName", target.getName(), "siegeType", event.getSiegeType()), new Color(255, 157, 0), null);
    }
}
