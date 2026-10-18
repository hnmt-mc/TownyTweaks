package me.darthpeti.townytweaks.towny.util;

import org.bukkit.configuration.ConfigurationSection;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

/** webhook.yml の読み取り。キーが欠けても、同梱の既定値で補って例外にしない。 */
public final class WebhookSettings {
    private static final Set<String> HOSTS = Set.of("discord.com", "discordapp.com", "ptb.discord.com", "canary.discord.com");
    private final ConfigurationSection user;
    private final ConfigurationSection defaults;

    public WebhookSettings(ConfigurationSection user, ConfigurationSection defaults) {
        this.user = user; this.defaults = defaults;
    }

    private Object value(String path) {
        Object value = user.get(path);
        return value != null ? value : defaults.get(path);
    }

    private boolean bool(String path, boolean fallback) {
        Object value = value(path);
        if (value instanceof Boolean flag) return flag;
        if (value instanceof String text) return text.trim().equalsIgnoreCase("true");
        return fallback;
    }

    private String text(String path) {
        Object value = value(path);
        return value == null ? "" : String.valueOf(value);
    }

    public boolean enabled(String event) { return bool("notification-" + event, false); }
    public boolean embed(String event) { return bool("embed-" + event, true); }
    public String message(String event) { return text("message-" + event); }
    public String title(String event) { return text("title-" + event); }
    public boolean faceEnabled() { return bool("face-enabled", true); }
    public String faceUrlTemplate() { return text("face-url"); }

    /** Discord の Webhook URL（https の公式ホストだけ）。未設定・不正なら null。 */
    public String webhookUrl() {
        String url = text("webhook-url").trim();
        if (url.isEmpty() || url.contains("PUTSOMETHINGHERE")) return null;
        try {
            URI uri = new URI(url);
            if (!"https".equals(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null || uri.getPort() != -1) return null;
            if (!HOSTS.contains(uri.getHost().toLowerCase(Locale.ROOT))) return null;
            if (uri.getPath() == null || !uri.getPath().startsWith("/api/webhooks/")) return null;
        } catch (java.net.URISyntaxException e) {
            return null;
        }
        return url;
    }
}
