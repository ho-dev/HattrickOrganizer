package core.file.xml;

import core.util.HOLogger;
import core.util.XMLUtils;
import module.youth.YouthLeague;
import org.jetbrains.annotations.Nullable;

import static core.file.xml.XMLYouthTeamDetailsParser.optDate;
import static core.file.xml.XMLYouthTeamDetailsParser.optInt;
import static core.file.xml.XMLYouthTeamDetailsParser.optString;

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
            final var leagueId = optInt(root, YOUTH_LEAGUE_ID);
            final var season = optInt(root, SEASON);
            if (leagueId == null || season == null) {
                return null;
            }
            final var league = new YouthLeague();
            league.setYouthLeagueId(leagueId);
            league.setSeason(season);
            league.setYouthLeagueName(optString(root, "YouthLeagueName"));
            league.setYouthLeagueType(optInt(root, "YouthLeagueType"));
            league.setLastMatchRound(optInt(root, "LastMatchRound"));
            league.setNrOfTeamsInLeague(optInt(root, "NrOfTeamsInLeague"));
            league.setLeagueSystemId(optInt(root, "LeagueSystemID"));
            XMLUtils.findChildElement(root, "Teams").ifPresent(teams -> {
                for (var t : XMLUtils.getChildElements(teams, "Team")) {
                    final var team = new YouthLeague.Team();
                    team.setYouthLeagueId(leagueId);
                    team.setSeason(season);
                    team.setTeamId(XMLUtils.getIntValue(t, "TeamID"));
                    // the docs type it unsigned Integer; it is the name
                    team.setTeamName(optString(t, "TeamName"));
                    team.setPosition(optInt(t, "Position"));
                    team.setPositionChange(optInt(t, "PositionChange"));
                    team.setMatches(optInt(t, "Matches"));
                    team.setGoalsFor(optInt(t, "GoalsFor"));
                    team.setGoalsAgainst(optInt(t, "GoalsAgainst"));
                    team.setPoints(optInt(t, "Points"));
                    team.setWon(optInt(t, "Won"));
                    team.setDraws(optInt(t, "Draws"));
                    team.setLost(optInt(t, "Lost"));
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
            final var leagueId = optInt(root, YOUTH_LEAGUE_ID);
            final var season = optInt(root, SEASON);
            if (leagueId == null || leagueId != league.getYouthLeagueId() || season == null || season != league.getSeason()) {
                return false;
            }
            league.getFixtures().clear();
            XMLUtils.findChildElement(root, "Matches").ifPresent(matches -> {
                for (var m : XMLUtils.getChildElements(matches, "Match")) {
                    final var fixture = new YouthLeague.Fixture();
                    fixture.setMatchId(XMLUtils.getIntValue(m, "MatchID"));
                    fixture.setYouthLeagueId(leagueId);
                    fixture.setSeason(season);
                    fixture.setMatchRound(optInt(m, "MatchRound"));
                    fixture.setStatus(optString(m, "Status"));
                    XMLUtils.findChildElement(m, "HomeTeam").ifPresent(h -> {
                        fixture.setHomeTeamId(optInt(h, "HomeTeamID"));
                        fixture.setHomeTeamName(optString(h, "HomeTeamName"));
                    });
                    XMLUtils.findChildElement(m, "AwayTeam").ifPresent(a -> {
                        fixture.setAwayTeamId(optInt(a, "AwayTeamID"));
                        fixture.setAwayTeamName(optString(a, "AwayTeamName"));
                    });
                    fixture.setMatchDate(optDate(m, "MatchDate"));
                    fixture.setHomeGoals(optInt(m, "HomeGoals"));
                    fixture.setAwayGoals(optInt(m, "AwayGoals"));
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
