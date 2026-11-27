package core.file.xml;

import core.util.HOLogger;
import core.util.XMLUtils;
import module.youth.YouthLeague;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/** youthleaguedetails 1.1 (league and standings) and youthleaguefixtures 1.0 (the season's matches). */
public final class XMLYouthLeagueParser {

    private static final String YOUTH_LEAGUE_ID = "YouthLeagueID";
    private static final String SEASON = "Season";

    private XMLYouthLeagueParser() {
    }

    /** @return the league with its standings, null for an empty response or a CHPP error */
    public static @Nullable YouthLeague parseDetails(String xml) {
        final var doc = XMLManager.parseString(xml);
        if (doc == null) {
            return null;
        }
        try {
            final var root = doc.getDocumentElement();
            final var leagueId = XMLUtils.findIntegerValue(root, YOUTH_LEAGUE_ID);
            final var season = XMLUtils.findIntegerValue(root, SEASON);
            if (leagueId.isEmpty() || season.isEmpty()) {
                return null;
            }
            final var league = new YouthLeague();
            league.setYouthLeagueId(leagueId.get());
            league.setSeason(season.get());
            XMLUtils.findStringValue(root, "YouthLeagueName").ifPresent(league::setYouthLeagueName);
            XMLUtils.findIntegerValue(root, "YouthLeagueType").ifPresent(league::setYouthLeagueType);
            XMLUtils.findIntegerValue(root, "LastMatchRound").ifPresent(league::setLastMatchRound);
            XMLUtils.findIntegerValue(root, "NrOfTeamsInLeague").ifPresent(league::setNrOfTeamsInLeague);
            XMLUtils.findIntegerValue(root, "LeagueSystemID").ifPresent(league::setLeagueSystemId);
            XMLUtils.findChildElement(root, "Teams").ifPresent(teams -> {
                for (final var t : XMLUtils.getChildElements(teams, "Team")) {
                    final var team = new YouthLeague.Team();
                    team.setYouthLeagueId(leagueId.get());
                    team.setSeason(season.get());
                    team.setTeamId(XMLUtils.getIntValue(t, "TeamID"));
                    // the docs type it unsigned Integer; it is the name
                    XMLUtils.findStringValue(t, "TeamName").ifPresent(team::setTeamName);
                    XMLUtils.findIntegerValue(t, "Position").ifPresent(team::setPosition);
                    XMLUtils.findIntegerValue(t, "PositionChange").ifPresent(team::setPositionChange);
                    XMLUtils.findIntegerValue(t, "Matches").ifPresent(team::setMatches);
                    XMLUtils.findIntegerValue(t, "GoalsFor").ifPresent(team::setGoalsFor);
                    XMLUtils.findIntegerValue(t, "GoalsAgainst").ifPresent(team::setGoalsAgainst);
                    XMLUtils.findIntegerValue(t, "Points").ifPresent(team::setPoints);
                    XMLUtils.findIntegerValue(t, "Won").ifPresent(team::setWon);
                    XMLUtils.findIntegerValue(t, "Draws").ifPresent(team::setDraws);
                    XMLUtils.findIntegerValue(t, "Lost").ifPresent(team::setLost);
                    league.getTeams().add(team);
                }
            });
            return league;
        } catch (Exception e) {
            HOLogger.instance().log(XMLYouthLeagueParser.class, e);
            return null;
        }
    }

    /**
     * Adds the fixtures to the league they belong to.
     *
     * @return false when the response is unreadable or belongs to another league or season
     */
    public static boolean parseFixtures(String xml, YouthLeague league) {
        final var doc = XMLManager.parseString(xml);
        if (doc == null) {
            return false;
        }
        try {
            final var root = doc.getDocumentElement();
            final var leagueId = XMLUtils.findIntegerValue(root, YOUTH_LEAGUE_ID);
            final var season = XMLUtils.findIntegerValue(root, SEASON);
            if (!leagueId.equals(Optional.of(league.getYouthLeagueId())) || !season.equals(Optional.of(league.getSeason()))) {
                return false;
            }
            league.getFixtures().clear();
            XMLUtils.findChildElement(root, "Matches").ifPresent(matches -> {
                for (final var m : XMLUtils.getChildElements(matches, "Match")) {
                    final var fixture = new YouthLeague.Fixture();
                    fixture.setMatchId(XMLUtils.getIntValue(m, "MatchID"));
                    fixture.setYouthLeagueId(league.getYouthLeagueId());
                    fixture.setSeason(league.getSeason());
                    XMLUtils.findIntegerValue(m, "MatchRound").ifPresent(fixture::setMatchRound);
                    XMLUtils.findStringValue(m, "Status").ifPresent(fixture::setStatus);
                    XMLUtils.findChildElement(m, "HomeTeam").ifPresent(h -> {
                        XMLUtils.findIntegerValue(h, "HomeTeamID").ifPresent(fixture::setHomeTeamId);
                        XMLUtils.findStringValue(h, "HomeTeamName").ifPresent(fixture::setHomeTeamName);
                    });
                    XMLUtils.findChildElement(m, "AwayTeam").ifPresent(a -> {
                        XMLUtils.findIntegerValue(a, "AwayTeamID").ifPresent(fixture::setAwayTeamId);
                        XMLUtils.findStringValue(a, "AwayTeamName").ifPresent(fixture::setAwayTeamName);
                    });
                    XMLUtils.findHODateTimeValue(m, "MatchDate").ifPresent(fixture::setMatchDate);
                    XMLUtils.findIntegerValue(m, "HomeGoals").ifPresent(fixture::setHomeGoals);
                    XMLUtils.findIntegerValue(m, "AwayGoals").ifPresent(fixture::setAwayGoals);
                    league.getFixtures().add(fixture);
                }
            });
            return true;
        } catch (Exception e) {
            HOLogger.instance().log(XMLYouthLeagueParser.class, e);
            return false;
        }
    }
}
