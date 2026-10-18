package me.darthpeti.townytweaks.towny.util;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DiscordWebhookTest {
    private static DiscordWebhook webhook() { return new DiscordWebhook("https://discord.com/api/webhooks/1/x"); }

    @Test void escapesQuotesBackslashesAndNewlinesSoNamesCannotBreakTheJson() {
        var hook = webhook();
        hook.setContent("a\"b\\c\nd\te");
        var json = hook.toJson();
        assertTrue(json.contains("\"content\":\"a\\\"b\\\\c\\nd\\te\""), json);
    }

    @Test void escapesControlCharacters() {
        var hook = webhook();
        hook.setContent("x\u0001y");
        assertTrue(hook.toJson().contains("x\\u0001y"));
    }

    @Test void neverPingsAnyone() {
        var hook = webhook();
        hook.setContent("@everyone");
        assertTrue(hook.toJson().contains("\"allowed_mentions\":{\"parse\":[]}"), hook.toJson());
    }

    @Test void embedCarriesTitleDescriptionColorAndThumbnail() {
        var hook = webhook();
        hook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("町ができました").setDescription("本文")
                .setColor(new Color(0, 81, 255)).setThumbnail("https://mc-heads.net/avatar/abc/128"));
        var json = hook.toJson();
        assertTrue(json.contains("\"title\":\"町ができました\""));
        assertTrue(json.contains("\"description\":\"本文\""));
        assertTrue(json.contains("\"color\":" + ((0 << 16) + (81 << 8) + 255)));
        assertTrue(json.contains("\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/abc/128\"}"));
    }

    @Test void embedWithoutThumbnailOmitsIt() {
        var hook = webhook();
        hook.addEmbed(new DiscordWebhook.EmbedObject().setDescription("本文"));
        assertFalse(hook.toJson().contains("thumbnail"));
    }

    @Test void bodyIsEncodedAsUtf8() {
        var hook = webhook();
        hook.setContent("町");
        assertArrayEquals(hook.toJson().getBytes(StandardCharsets.UTF_8), hook.body());
        assertTrue(new String(hook.body(), StandardCharsets.UTF_8).contains("\"町\""));
    }

    @Test void requiresContentOrEmbed() {
        assertThrows(IllegalArgumentException.class, () -> webhook().toJson());
    }
}
