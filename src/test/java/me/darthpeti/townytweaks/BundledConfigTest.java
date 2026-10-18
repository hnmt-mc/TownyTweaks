package me.darthpeti.townytweaks;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BundledConfigTest {
    @Test void keepsTheSameKeysAndDefaultsWithJapaneseText() {
        var stream = getClass().getClassLoader().getResourceAsStream("config.yml");
        assertNotNull(stream);
        var yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        assertEquals(Set.of("townytweaks-prefix", "enable-armorstand-restriction", "allow-armorstand-only-in-plottype",
                "enable-shulker-restriction", "allow-shulkers-only-in-plottype", "enable-brew-barrels-restriction",
                "allow-barrels-only-in-plottype", "allow-pearl-in-besieged-towns", "spawn-trap-prevention-in-capitals"), yaml.getKeys(false));
        assertEquals("§e[§bTownyTweaks§e]:§f ", yaml.getString("townytweaks-prefix"));
        for (String key : yaml.getKeys(false))
            if (yaml.get(key) instanceof Boolean flag) assertFalse(flag, key + " は既定で無効のまま");
        assertEquals("edit-plot-type-name-here", yaml.getString("allow-barrels-only-in-plottype"));
    }
}
