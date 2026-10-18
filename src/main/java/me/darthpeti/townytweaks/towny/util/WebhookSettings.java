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

    /** 埋め込みの項目欄の定義（「ラベル|値」の形で書く）。 */
    public record FieldTemplate(String label, String value) { }

    public java.util.List<FieldTemplate> fields(String event) {
        Object raw = value("fields-" + event);
        var result = new java.util.ArrayList<FieldTemplate>();
        if (!(raw instanceof java.util.List<?> list)) return result;
        for (Object item : list) {
            if (!(item instanceof String text)) continue;
            int split = text.indexOf('|');
            if (split <= 0) continue;
            String label = text.substring(0, split).trim();
            String value = text.substring(split + 1).trim();
            if (!label.isEmpty() && !value.isEmpty()) result.add(new FieldTemplate(label, value));
        }
        return result;
    }

    /** 町・国がなくなった理由の日本語。group は "town" か "nation"。 */
    public String reason(String group, String cause) {
        if (cause != null) {
            String text = text("reasons-" + group + "." + cause.toLowerCase(Locale.ROOT));
            if (!text.isEmpty()) return text;
        }
        String unknown = text("reasons-unknown");
        return unknown.isEmpty() ? "-" : unknown;
    }

    /** 「国なし」などに使う文言。 */
    public String noneText() {
        String text = text("text-none");
        return text.isEmpty() ? "なし" : text;
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
