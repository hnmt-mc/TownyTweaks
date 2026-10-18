package me.darthpeti.townytweaks.towny.util;

import java.net.URI;
import java.util.UUID;

/** 顔画像のURLを組み立てる。https のテンプレートだけを受け付け、安全でなければ null を返す。 */
public final class FaceUrl {
    private static final int MAX_LENGTH = 300;

    private FaceUrl() { }

    public static String build(String template, UUID uuid) {
        if (template == null || uuid == null || !template.startsWith("https://")) return null;
        if (!template.contains("%uuid%") && !template.contains("%uuid_dashed%")) return null;
        String url = template.replace("%uuid_dashed%", uuid.toString()).replace("%uuid%", uuid.toString().replace("-", ""));
        if (url.length() > MAX_LENGTH) return null;
        for (int i = 0; i < url.length(); i++) {
            char c = url.charAt(i);
            if (c <= 0x20 || c == 0x7f) return null;
        }
        try {
            URI uri = new URI(url);
            if (uri.getHost() == null || uri.getHost().isEmpty() || uri.getUserInfo() != null) return null;
        } catch (java.net.URISyntaxException e) {
            return null;
        }
        return url;
    }
}
