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
            "town-creation", Set.of("townName", "mayorName", "residents", "nationName"),
            "town-ruins", Set.of("townName", "mayorName", "residents", "blocks", "nationName"),
            "town-delete", Set.of("townName", "mayorName", "blocks", "reason"),
            "nation-create", Set.of("nationName", "kingName", "capitalName", "towns", "residents"),
            "nation-disband", Set.of("nationName", "kingName", "reason"),
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
            var fields = new WebhookSettings(new YamlConfiguration(), yaml).fields(event);
            for (var field : fields) {
                var matcher = placeholder.matcher(field.label() + field.value());
                while (matcher.find()) assertTrue(allowed.contains(matcher.group(1)), "fields-" + event + " uses {" + matcher.group(1) + "}");
            }
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

    @Test void everyTownyDeletionCauseHasAJapaneseReason() {
        var settings = new WebhookSettings(new YamlConfiguration(), bundled());
        for (var cause : com.palmergames.bukkit.towny.event.DeleteTownEvent.Cause.values())
            assertNotEquals(settings.reason("town", "no-such-cause"), settings.reason("town", cause.name()), "town: " + cause);
        for (var cause : com.palmergames.bukkit.towny.event.DeleteNationEvent.Cause.values())
            assertNotEquals(settings.reason("nation", "no-such-cause"), settings.reason("nation", cause.name()), "nation: " + cause);
    }

    @Test void deletionAndCreationNotificationsListTheDetailsPlayersAskAbout() {
        var settings = new WebhookSettings(new YamlConfiguration(), bundled());
        assertTrue(settings.fields("town-delete").stream().anyMatch(f -> f.value().contains("{reason}")));
        assertTrue(settings.fields("nation-disband").stream().anyMatch(f -> f.value().contains("{reason}")));
        assertTrue(settings.fields("nation-create").stream().anyMatch(f -> f.value().contains("{towns}")));
        assertFalse(settings.noneText().isBlank());
    }
}
