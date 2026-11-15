package core.file.xml;

import core.db.DBManager;
import core.util.HODateTime;
import core.util.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/** youthteamdetails 1.5: a team built from the docs, and the real answer for a club without one. */
class XMLYouthTeamDetailsParserTest {

    private static final int HRF = 910_100;

    @Test
    void aClubWithoutYouthTeamReadsAsNone() throws IOException {
        // real response of 2026-11-15: an empty team of id 0 rather than an error
        final var xml = ResourceUtils.getResourceFileAsString("chpp/youth/youthteamdetails_1.5_noTeam.xml");

        assertThat(XMLYouthTeamDetailsParser.parse(xml, HRF)).isNull();
        assertThat(XMLYouthTeamDetailsParser.parse("", HRF)).isNull();
    }

    private static String fixture() throws IOException {
        return Objects.requireNonNull(ResourceUtils.getResourceFileAsString("chpp/youth/youthteamdetails_1.5.xml"));
    }

    @Test
    void teamFields() throws IOException {
        final var details = XMLYouthTeamDetailsParser.parse(fixture(), HRF);

        assertThat(details).isNotNull();
        assertThat(details.getHrfId()).isEqualTo(HRF);
        assertThat(details.getYouthTeamId()).isEqualTo(3256333);
        assertThat(details.getYouthTeamName()).isEqualTo("JFC Akademie");
        assertThat(details.getShortTeamName()).isEqualTo("JFC A");
        assertThat(details.getCreatedDate()).isEqualTo(HODateTime.fromHT("2024-03-02 11:20:00"));
        assertThat(details.getUserId()).isEqualTo(1357154);
        assertThat(details.getGenderId()).isEqualTo(1);
        assertThat(details.getLeagueSystemId()).isEqualTo(1);
        assertThat(details.getPlayingMatch()).isFalse();
        assertThat(details.getCountryId()).isEqualTo(3);
        assertThat(details.getCountryName()).isEqualTo("Deutschland");
        assertThat(details.getRegionId()).isEqualTo(229);
        assertThat(details.getRegionName()).isEqualTo("Berlin");
        assertThat(details.getYouthArenaId()).isEqualTo(3236247);
        assertThat(details.getYouthArenaName()).isEqualTo("JFC-Zentrale");
        assertThat(details.getYouthLeagueId()).isEqualTo(685791);
        assertThat(details.getYouthLeagueName()).isEqualTo("Berliner Jugendliga");
        assertThat(details.getYouthLeagueStatus()).isEqualTo(1);
        assertThat(details.getMotherTeamId()).isEqualTo(2168446);
        assertThat(details.getMotherTeamName()).isEqualTo("JFC");
        assertThat(details.getTrainerYouthPlayerId()).isEqualTo(364643130);
        assertThat(details.getNextTrainingMatchDate()).isEqualTo(HODateTime.fromHT("2026-11-18 00:00:00"));
    }

    @Test
    void scoutFields() throws IOException {
        final var details = XMLYouthTeamDetailsParser.parse(fixture(), HRF);

        assertThat(details).isNotNull();
        assertThat(details.getScouts()).hasSize(2);
        final var travelling = details.getScouts().get(0);
        assertThat(travelling.getYouthScoutId()).isEqualTo(382876);
        assertThat(travelling.getScoutName()).isEqualTo("Joerg Hopfen");
        assertThat(travelling.getAge()).isEqualTo(41);
        assertThat(travelling.getRegionName()).isEqualTo("Berlin");
        assertThat(travelling.getInCountryId()).isEqualTo(46);
        assertThat(travelling.getInRegionName()).isEqualTo("Zürich");
        assertThat(travelling.getHiredDate()).isEqualTo(HODateTime.fromHT("2025-01-05 12:00:00"));
        assertThat(travelling.getLastCalled()).isEqualTo(HODateTime.fromHT("2026-11-10 08:30:00"));
        assertThat(travelling.getPlayerTypeSearch()).isEqualTo(3);
        assertThat(travelling.getHofPlayerId()).isZero();
        assertThat(travelling.getTravelStartDate()).isEqualTo(HODateTime.fromHT("2026-11-12 09:00:00"));
        assertThat(travelling.getTravelLength()).isEqualTo(650);
        assertThat(travelling.getTravelType()).isEqualTo(2);
        final var home = details.getScouts().get(1);
        assertThat(home.getHofPlayerId()).isEqualTo(123456);
        assertThat(home.getTravelStartDate()).isNull();
        assertThat(home.getTravelLength()).isNull();
    }

    @Test
    void teamWithoutLeagueOrOwnerLeavesThoseNull() throws IOException {
        final var xml = fixture()
                .replaceAll("(?s)<YouthLeague>.*</YouthLeague>", "<YouthLeague />")
                .replaceAll("(?s)<OwningTeam>.*</OwningTeam>", "<OwningTeam />")
                .replaceAll("(?s)<ScoutList>.*</ScoutList>", "<ScoutList />");

        final var details = XMLYouthTeamDetailsParser.parse(xml, HRF);

        assertThat(details).isNotNull();
        assertThat(details.getYouthLeagueId()).isNull();
        assertThat(details.getYouthLeagueName()).isNull();
        assertThat(details.getMotherTeamId()).isNull();
        assertThat(details.getScouts()).isEmpty();
    }

    @Test
    void storedAndLoadedPerHrf() throws IOException {
        final var details = XMLYouthTeamDetailsParser.parse(fixture(), HRF);
        final var db = DBManager.instance();

        db.storeYouthTeamDetails(details);
        // storing the same HRF again replaces it
        db.storeYouthTeamDetails(details);
        final var loaded = db.loadYouthTeamDetails(HRF);

        assertThat(loaded).usingRecursiveComparison().ignoringFields("isStored", "scouts.isStored").isEqualTo(details);
        assertThat(loaded.getScouts()).hasSize(2);
        assertThat(db.loadYouthTeamDetails(HRF + 1)).isNull();
    }
}
