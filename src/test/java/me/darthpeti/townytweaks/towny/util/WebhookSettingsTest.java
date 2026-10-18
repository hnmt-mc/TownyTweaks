package me.darthpeti.townytweaks.towny.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebhookSettingsTest {
    private static YamlConfiguration yaml(String text) {
        var yaml = new YamlConfiguration();
        try { yaml.loadFromString(text); } catch (Exception e) { throw new IllegalStateException(e); }
        return yaml;
    }
    private static final String DEFAULTS = """
            webhook-url: "https://discord.com/api/webhooks/1/token"
            face-enabled: true
            face-url: "https://mc-heads.net/avatar/%uuid%/128"
            notification-town-creation: "false"
            message-town-creation: "default message"
            title-town-creation: "default title"
            embed-town-creation: "true"
            """;

    @Test void readsBooleansWrittenAsTextOrAsYamlBooleans() {
        var user = yaml("notification-town-creation: \"TRUE\"\nembed-town-creation: false\n");
        var settings = new WebhookSettings(user, yaml(DEFAULTS));
        assertTrue(settings.enabled("town-creation"));
        assertFalse(settings.embed("town-creation"));
    }

    @Test void missingKeysFallBackToTheBundledDefaultsInsteadOfThrowing() {
        var settings = new WebhookSettings(yaml(""), yaml(DEFAULTS));
        assertFalse(settings.enabled("town-creation"));
        assertTrue(settings.embed("town-creation"));
        assertEquals("default message", settings.message("town-creation"));
        assertEquals("default title", settings.title("town-creation"));
        assertTrue(settings.faceEnabled());
        assertEquals("https://mc-heads.net/avatar/%uuid%/128", settings.faceUrlTemplate());
    }

    @Test void unknownEventsAreDisabledAndHaveNoText() {
        var settings = new WebhookSettings(yaml(""), yaml(DEFAULTS));
        assertFalse(settings.enabled("nope"));
        assertEquals("", settings.message("nope"));
        assertEquals("", settings.title("nope"));
    }

    @Test void userValuesWinOverDefaults() {
        var settings = new WebhookSettings(yaml("message-town-creation: \"mine\"\nface-enabled: false\n"), yaml(DEFAULTS));
        assertEquals("mine", settings.message("town-creation"));
        assertFalse(settings.faceEnabled());
    }

    @Test void acceptsOnlyDiscordHttpsWebhookUrls() {
        for (String ok : new String[]{"https://discord.com/api/webhooks/1/abc", "https://discordapp.com/api/webhooks/1/abc",
                "https://canary.discord.com/api/webhooks/1/abc", "https://ptb.discord.com/api/webhooks/1/abc"})
            assertEquals(ok, new WebhookSettings(yaml("webhook-url: \"" + ok + "\""), yaml(DEFAULTS)).webhookUrl());
        for (String bad : new String[]{"http://discord.com/api/webhooks/1/abc", "https://evil.example/api/webhooks/1/abc",
                "https://discord.com.evil.example/api/webhooks/1/abc", "https://discord.com/other", "",
                "https://discord.com/api/webhooks/PUTSOMETHINGHERE", "https://user@discord.com/api/webhooks/1/abc"})
            assertNull(new WebhookSettings(yaml("webhook-url: \"" + bad + "\""), yaml(DEFAULTS)).webhookUrl(), bad);
    }
}
