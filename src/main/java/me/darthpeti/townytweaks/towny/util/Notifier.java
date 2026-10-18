package me.darthpeti.townytweaks.towny.util;

import me.darthpeti.townytweaks.Main;
import org.bukkit.Bukkit;

import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/** 設定を読んで Webhook を組み立て、メインスレッドを止めないよう別スレッドで送る。 */
public final class Notifier {
    private Notifier() { }

    private static WebhookSettings settings() {
        Main plugin = Main.getInstance();
        return new WebhookSettings(plugin.getCustomConfig(), plugin.getBundledWebhookConfig());
    }

    /** 所属する国の名前。国がなければ「なし」にあたる言葉。 */
    public static String nationNameOrNone(com.palmergames.bukkit.towny.object.Town town) {
        var nation = town.getNationOrNull();
        return nation != null ? nation.getName() : settings().noneText();
    }

    /** 町・国がなくなった理由の日本語。group は "town" か "nation"。 */
    public static String reason(String group, Enum<?> cause) {
        return settings().reason(group, cause == null ? null : cause.name());
    }

    public static void send(String event, Map<String, String> values, Color color, UUID faceOwner) {
        Main plugin = Main.getInstance();
        WebhookSettings settings = settings();
        DiscordWebhook webhook;
        try {
            webhook = NotificationFactory.create(settings, event, values, color, faceOwner);
        } catch (RuntimeException error) {
            plugin.getLogger().log(Level.WARNING, "Discord notification could not be built; event=" + event, error);
            return;
        }
        if (webhook == null) {
            if (settings.enabled(event) && settings.webhookUrl() == null)
                plugin.getLogger().warning("webhook.yml の webhook-url が未設定か、Discord の URL ではありません（event=" + event + "）");
            return;
        }
        final DiscordWebhook toSend = webhook;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                toSend.execute();
            } catch (java.io.IOException error) {
                plugin.getLogger().warning("Discord notification failed; event=" + event + ", reason=" + error.getMessage());
            } catch (RuntimeException error) {
                plugin.getLogger().log(Level.WARNING, "Discord notification failed; event=" + event, error);
            }
        });
    }
}
