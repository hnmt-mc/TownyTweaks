package me.darthpeti.townytweaks.towny.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationFactoryTest {
    private static final UUID ID = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");

    private static WebhookSettings settings(String extra) {
        var yaml = new YamlConfiguration();
        try {
            yaml.loadFromString("""
                    webhook-url: "https://discord.com/api/webhooks/1/token"
                    face-enabled: true
                    face-url: "https://mc-heads.net/avatar/%uuid%/128"
                    notification-town-creation: true
                    message-town-creation: "**{townName}** を {mayorName} が作りました"
                    title-town-creation: "町ができました"
                    embed-town-creation: true
                    notification-town-delete: false
                    message-town-delete: "{townName} が消えました"
                    """ + extra);
        } catch (Exception e) { throw new IllegalStateException(e); }
        return new WebhookSettings(yaml, new YamlConfiguration());
    }
    private static Map<String, String> values() { return Map.of("townName", "桜町", "mayorName", "taro"); }

    @Test void buildsAnEmbedWithTitleBodyColorAndTheMayorsFace() {
        var json = NotificationFactory.create(settings(""), "town-creation", values(), new Color(1, 2, 3), ID).toJson();
        assertTrue(json.contains("\"title\":\"町ができました\""), json);
        assertTrue(json.contains("**桜町** を taro が作りました"), json);
        assertTrue(json.contains("\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/069a79f444e94726a5befca90e38aaf5/128\"}"), json);
        assertTrue(json.contains("\"color\":" + ((1 << 16) + (2 << 8) + 3)), json);
    }

    @Test void omitsTheFaceWhenDisabledMissingOrTheTemplateIsUnsafe() {
        assertFalse(NotificationFactory.create(settings("face-enabled: false\n"), "town-creation", values(), Color.RED, ID).toJson().contains("thumbnail"));
        assertFalse(NotificationFactory.create(settings(""), "town-creation", values(), Color.RED, null).toJson().contains("thumbnail"));
        assertFalse(NotificationFactory.create(settings("face-url: \"http://evil.example/%uuid%\"\n"), "town-creation", values(), Color.RED, ID).toJson().contains("thumbnail"));
    }

    @Test void plainTextModeSendsTheMessageAsContent() {
        var json = NotificationFactory.create(settings("embed-town-creation: false\n"), "town-creation", values(), Color.RED, ID).toJson();
        assertTrue(json.contains("\"content\":\"**桜町** を taro が作りました\""), json);
        assertFalse(json.contains("embeds"));
    }

    @Test void disabledEventsAndMissingWebhookUrlsProduceNothing() {
        assertNull(NotificationFactory.create(settings(""), "town-delete", values(), Color.RED, ID));
        assertNull(NotificationFactory.create(settings("webhook-url: \"\"\n"), "town-creation", values(), Color.RED, ID));
    }

    @Test void namesCannotInjectMarkdownMentionsOrOtherPlaceholders() {
        var json = NotificationFactory.create(settings(""), "town-creation",
                Map.of("townName", "@everyone_*x*", "mayorName", "{townName}"), Color.RED, ID).toJson();
        assertTrue(json.contains("**\\\\@everyone\\\\_\\\\*x\\\\*** を \\\\{townName} が作りました") || json.contains("**\\\\@everyone\\\\_\\\\*x\\\\*** を {townName} が作りました"), json);
        assertFalse(json.contains("@everyone_"), json);
    }
}
