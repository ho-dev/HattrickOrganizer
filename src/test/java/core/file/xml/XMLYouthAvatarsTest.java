package core.file.xml;

import core.model.player.PlayerAvatar;
import core.util.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/** youthavatars 1.2 (fixture built from the CHPP docs) through the senior avatar parser. */
class XMLYouthAvatarsTest {

    @Test
    void oneAvatarPerYouthPlayer() throws IOException {
        final var avatars = XMLAvatarsParser.parseYouthAvatarsFromString(
                ResourceUtils.getResourceFileAsString("chpp/youth/youthavatars_1.2.xml"));

        assertThat(avatars).extracting(PlayerAvatar::getPlayerID).containsExactly(364643130, 364643131);
    }

    @Test
    void theSeniorParserIgnoresTheYouthTree() throws IOException {
        assertThat(XMLAvatarsParser.parseAvatarsFromString(
                ResourceUtils.getResourceFileAsString("chpp/youth/youthavatars_1.2.xml"))).isEmpty();
    }
}
