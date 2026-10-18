package me.darthpeti.townytweaks.towny.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FaceUrlTest {
    private static final UUID ID = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");

    @Test void putsTheUuidWithoutDashesIntoTheTemplate() {
        assertEquals("https://mc-heads.net/avatar/069a79f444e94726a5befca90e38aaf5/128",
                FaceUrl.build("https://mc-heads.net/avatar/%uuid%/128", ID));
    }

    @Test void supportsTheDashedPlaceholder() {
        assertEquals("https://example.com/069a79f4-44e9-4726-a5be-fca90e38aaf5.png", FaceUrl.build("https://example.com/%uuid_dashed%.png", ID));
    }

    @Test void rejectsEverythingButHttps() {
        assertNull(FaceUrl.build("http://mc-heads.net/avatar/%uuid%", ID));
        assertNull(FaceUrl.build("ftp://mc-heads.net/avatar/%uuid%", ID));
        assertNull(FaceUrl.build("javascript:alert(1)//%uuid%", ID));
    }

    @Test void rejectsTemplatesWithoutAPlaceholderOrWithUnsafeParts() {
        assertNull(FaceUrl.build("https://mc-heads.net/avatar/steve", ID));
        assertNull(FaceUrl.build("https://user:pass@mc-heads.net/%uuid%", ID));
        assertNull(FaceUrl.build("https://mc-heads.net/%uuid%\n/x", ID));
        assertNull(FaceUrl.build("https://mc-heads.net/%uuid% x", ID));
        assertNull(FaceUrl.build("https:///%uuid%", ID));
    }

    @Test void rejectsMissingInputsAndTooLongUrls() {
        assertNull(FaceUrl.build(null, ID));
        assertNull(FaceUrl.build("", ID));
        assertNull(FaceUrl.build("https://mc-heads.net/avatar/%uuid%", null));
        assertNull(FaceUrl.build("https://example.com/" + "a".repeat(300) + "/%uuid%", ID));
    }
}
