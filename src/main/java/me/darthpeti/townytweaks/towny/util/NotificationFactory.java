package me.darthpeti.townytweaks.towny.util;

import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 設定とイベントの値から、送る Webhook を組み立てる。無効なイベントや宛先なしのときは null。 */
public final class NotificationFactory {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z]+)}");

    private static final int MAX_FIELDS = 25;
    private static final int MAX_LABEL = 256;
    private static final int MAX_VALUE = 1024;

    private NotificationFactory() { }

    private static String cut(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max);
    }

    public static DiscordWebhook create(WebhookSettings settings, String event, Map<String, String> values, Color color, UUID faceOwner) {
        if (!settings.enabled(event)) return null;
        String url = settings.webhookUrl();
        if (url == null) return null;
        DiscordWebhook webhook = new DiscordWebhook(url);
        String message = fill(settings.message(event), values);
        if (settings.embed(event)) {
            var embed = new DiscordWebhook.EmbedObject().setColor(color).setDescription(message);
            String title = fill(settings.title(event), values);
            if (!title.isEmpty()) embed.setTitle(title);
            String face = settings.faceEnabled() ? FaceUrl.build(settings.faceUrlTemplate(), faceOwner) : null;
            if (face != null) embed.setThumbnail(face);
            int count = 0;
            for (var field : settings.fields(event)) {
                if (count >= MAX_FIELDS) break;
                String label = fill(field.label(), values).trim();
                String text = fill(field.value(), values).trim();
                if (label.isEmpty() || text.replaceAll("[*_~|`\\s]", "").isEmpty()) continue;
                embed.addField(cut(label, MAX_LABEL), cut(text, MAX_VALUE), true);
                count++;
            }
            webhook.addEmbed(embed);
        } else {
            webhook.setContent(message);
        }
        return webhook;
    }

    /** {name} を一度だけ置き換える（値に {…} が含まれても、再び置き換えない）。値の Markdown は無効にする。 */
    static String fill(String template, Map<String, String> values) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String value = values.get(matcher.group(1));
            matcher.appendReplacement(out, Matcher.quoteReplacement(value == null ? matcher.group() : escape(value)));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    static String escape(String value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ("\\*_~|`>@".indexOf(c) >= 0) out.append('\\');
            out.append(c);
        }
        return out.toString();
    }
}
