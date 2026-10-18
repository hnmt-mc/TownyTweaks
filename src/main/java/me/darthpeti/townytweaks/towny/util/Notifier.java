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

    public static void send(String event, Map<String, String> values, Color color, UUID faceOwner) {
        Main plugin = Main.getInstance();
        WebhookSettings settings = new WebhookSettings(plugin.getCustomConfig(), plugin.getBundledWebhookConfig());
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
