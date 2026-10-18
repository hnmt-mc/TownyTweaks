package me.darthpeti.townytweaks.towny.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class BundledWebhookConfigTest {
    private static final Map<String, Set<String>> EVENTS = Map.of(
            "town-creation", Set.of("townName", "mayorName"),
            "town-ruins", Set.of("townName"),
            "town-delete", Set.of("townName", "mayorName"),
            "nation-create", Set.of("nationName", "kingName"),
            "nation-disband", Set.of("nationName", "kingName"),
            "siegewar-session-start", Set.of(),
            "siegewar-session-end", Set.of(),
            "siegewar-siege-start", Set.of("attacker", "defender", "townName", "siegeType"));

    private static YamlConfiguration bundled() {
        var stream = BundledWebhookConfigTest.class.getClassLoader().getResourceAsStream("webhook.yml");
        assertNotNull(stream);
        return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    @Test void everyEventHasAllKeysAndOnlyKnownPlaceholders() {
        var yaml = bundled();
        var placeholder = Pattern.compile("\\{([A-Za-z]+)}");
        EVENTS.forEach((event, allowed) -> {
            for (String prefix : new String[]{"notification-", "embed-", "title-", "message-"})
                assertNotNull(yaml.get(prefix + event), prefix + event);
            for (String key : new String[]{"title-" + event, "message-" + event}) {
                var matcher = placeholder.matcher(yaml.getString(key));
                while (matcher.find()) assertTrue(allowed.contains(matcher.group(1)), key + " uses {" + matcher.group(1) + "}");
            }
        });
    }

    @Test void shipsDisabledUntilConfiguredWithAJapaneseDefaultAndFace() {
        var settings = new WebhookSettings(new YamlConfiguration(), bundled());
        EVENTS.keySet().forEach(event -> assertFalse(settings.enabled(event), event));
        assertNull(settings.webhookUrl());
        assertTrue(settings.faceEnabled());
        assertNotNull(FaceUrl.build(settings.faceUrlTemplate(), java.util.UUID.randomUUID()));
        assertTrue(settings.title("town-creation").contains("町"));
    }
}
