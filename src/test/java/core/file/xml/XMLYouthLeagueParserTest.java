package core.file.xml;

import core.db.DBManager;
import core.util.HODateTime;
import core.util.ResourceUtils;
import module.youth.YouthLeague;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/** youthleaguedetails 1.1 and youthleaguefixtures 1.0 (fixtures built from the CHPP docs). */
class XMLYouthLeagueParserTest {

    private static String resource(String name) throws IOException {
        return Objects.requireNonNull(ResourceUtils.getResourceFileAsString("chpp/youth/" + name));
    }

    @Test
    void leagueAndStandings() throws IOException {
        final var league = XMLYouthLeagueParser.parseDetails(resource("youthleaguedetails_1.1.xml"));

        assertThat(league).isNotNull();
        assertThat(league.getYouthLeagueId()).isEqualTo(685791);
        assertThat(league.getSeason()).isEqualTo(94);
        assertThat(league.getYouthLeagueName()).isEqualTo("Berliner Jugendliga");
        assertThat(league.getYouthLeagueType()).isEqualTo(1);
        assertThat(league.getLastMatchRound()).isEqualTo(9);
        assertThat(league.getNrOfTeamsInLeague()).isEqualTo(8);
        assertThat(league.getLeagueSystemId()).isEqualTo(1);
        assertThat(league.getTeams()).hasSize(2);
        final var second = league.getTeams().get(1);
        assertThat(second.getTeamId()).isEqualTo(3256333);
        assertThat(second.getTeamName()).isEqualTo("JFC Akademie");
        assertThat(second.getPosition()).isEqualTo(2);
        assertThat(second.getPositionChange()).isEqualTo(1);
        assertThat(second.getMatches()).isEqualTo(9);
        assertThat(second.getGoalsFor()).isEqualTo(22);
        assertThat(second.getGoalsAgainst()).isEqualTo(12);
        assertThat(second.getPoints()).isEqualTo(19);
        assertThat(second.getWon()).isEqualTo(6);
        assertThat(second.getDraws()).isEqualTo(1);
        assertThat(second.getLost()).isEqualTo(2);
    }

    private static YouthLeague league() throws IOException {
        return Objects.requireNonNull(XMLYouthLeagueParser.parseDetails(resource("youthleaguedetails_1.1.xml")));
    }

    @Test
    void fixtures() throws IOException {
        final var league = league();

        assertThat(XMLYouthLeagueParser.parseFixtures(resource("youthleaguefixtures_1.0.xml"), league)).isTrue();

        assertThat(league.getFixtures()).hasSize(2);
        final var played = league.getFixtures().get(0);
        assertThat(played.getMatchId()).isEqualTo(146086858);
        assertThat(played.getMatchRound()).isEqualTo(9);
        assertThat(played.getStatus()).isEqualTo("2");
        assertThat(played.getHomeTeamId()).isEqualTo(3271684);
        assertThat(played.getAwayTeamName()).isEqualTo("JFC Akademie");
        assertThat(played.getMatchDate()).isEqualTo(HODateTime.fromHT("2026-11-14 19:15:00"));
        assertThat(played.getHomeGoals()).isEqualTo(2);
        assertThat(played.getAwayGoals()).isEqualTo(1);
        final var upcoming = league.getFixtures().get(1);
        assertThat(upcoming.getHomeGoals()).isNull();
        assertThat(upcoming.getAwayGoals()).isNull();
    }

    @Test
    void fixturesOfAnotherSeasonAreRefused() throws IOException {
        final var league = league();
        final var otherSeason = resource("youthleaguefixtures_1.0.xml").replace("<Season>94</Season>", "<Season>95</Season>");

        assertThat(XMLYouthLeagueParser.parseFixtures(otherSeason, league)).isFalse();
        assertThat(league.getFixtures()).isEmpty();
        assertThat(XMLYouthLeagueParser.parseDetails("")).isNull();
    }

    @Test
    void storedAndLoadedAsTheNewestSeason() throws IOException {
        final var league = league();
        XMLYouthLeagueParser.parseFixtures(resource("youthleaguefixtures_1.0.xml"), league);
        final var db = DBManager.instance();

        db.storeYouthLeague(league);
        db.storeYouthLeague(league);
        final var loaded = db.loadYouthLeague(685791);

        assertThat(loaded).usingRecursiveComparison()
                .ignoringFields("isStored", "teams.isStored", "fixtures.isStored").isEqualTo(league);
        assertThat(db.loadYouthLeague(1)).isNull();
    }
}
