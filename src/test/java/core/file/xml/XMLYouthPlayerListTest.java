package core.file.xml;

import core.util.ResourceUtils;
import module.youth.YouthPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/** youthplayerlist 1.4 read completely (fixture built from the CHPP docs, see its header). */
class XMLYouthPlayerListTest {

    @Test
    void nativeCountryIsRead() throws IOException {
        final var players = new XMLPlayersParser().parseYouthPlayersFromString(
                ResourceUtils.getResourceFileAsString("chpp/youth/youthplayerlist_1.4.xml"));

        assertThat(players).hasSize(1);
        final var player = players.get(0);
        assertThat(player)
                .containsEntry("YouthPlayerID", "364643130")
                .containsEntry(XMLPlayersParser.YOUTH_NATIVE_COUNTRY_ID, "3")
                .containsEntry(XMLPlayersParser.YOUTH_NATIVE_COUNTRY_NAME, "Deutschland")
                .containsEntry("GenderID", "1")
                .containsEntry("YouthMatchID", "146086858");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ScoutId", "ScoutID"})
    void scoutIdIsReadInBothSpellings(String tag) throws IOException {
        final var xml = Objects.requireNonNull(
                        ResourceUtils.getResourceFileAsString("chpp/youth/youthplayerlist_1.4.xml"))
                .replace("<ScoutId>", "<" + tag + ">")
                .replace("</ScoutId>", "</" + tag + ">");

        final var players = new XMLPlayersParser().parseYouthPlayersFromString(xml);

        assertThat(players).hasSize(1);
        assertThat(players.get(0)).containsEntry("ScoutId", "382876");
    }

    @Test
    void youthPlayerTakesTheNativeCountryFromTheHrf() {
        final var properties = new Properties();
        properties.setProperty("id", "364643130");
        properties.setProperty("nativecountryid", "3");
        properties.setProperty("nativecountryname", "Deutschland");

        final var player = new YouthPlayer(properties);

        assertThat(player.getNativeCountryId()).isEqualTo(3);
        assertThat(player.getNativeCountryName()).isEqualTo("Deutschland");
    }

    @Test
    void anOlderHrfHasNoNativeCountry() {
        final var properties = new Properties();
        properties.setProperty("id", "364643130");

        final var player = new YouthPlayer(properties);

        assertThat(player.getNativeCountryId()).isNull();
        assertThat(player.getNativeCountryName()).isNull();
    }
}
