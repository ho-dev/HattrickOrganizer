package module.teamAnalyzer.ht;

import core.db.DBManager;
import core.file.xml.TeamStats;
import core.file.xml.XMLManager;
import core.file.xml.XMLPlayersParser;
import core.model.match.MatchKurzInfo;
import core.net.MyConnector;
import core.net.OnlineWorker;
import core.util.HODateTime;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.*;

import java.time.temporal.ChronoUnit;
import java.util.*;

import org.w3c.dom.Document;

/**
 * Hattrick Download Helper class
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class HattrickManager {

    private static final Map<Integer, Map<String, String>> teamDetailsCache = new HashMap<>();
    private static final Map<Integer, Map<String, TeamStats>> seriesDetailsCache = new HashMap<>();

    /**
     * Method that downloads from Hattrick the available matches for the team <code>teamId</code>.
     * If manual filter, the last 30 are made available.
     * If auto filter, enough matches to supply the filter needs are available.
     *
     * <p>Recent tournament are added if on manual, or if they are wanted, in addition to
     * the number specified.
     *
     * @param teamId ID of team to download matches for
     * @param filter the match filter object.
     */
    public static void downloadMatches(final int teamId, Filter filter) {
   		int limit = Math.min(filter.getNumber(), 50);

   		// If on manual, disable all filters, and download 30 matches.
   		if (!filter.isAutomatic()) {
   			limit = 30;
   		}

        var start = HODateTime.now().minus(8*30, ChronoUnit.DAYS);
	    List<MatchKurzInfo> matches = OnlineWorker.getMatchArchive(teamId, start, false);
        if (matches != null) {
            Collections.reverse(matches); // Newest first
            for (MatchKurzInfo match : matches) {
                if (match.getMatchStatus() != MatchKurzInfo.FINISHED) {
                    continue;
                }

                boolean refresh = DBManager.instance().matchLineupIsNotStored(match.getMatchType(), match.getMatchID())
                        || !DBManager.instance().isMatchIFKRatingInDB(match.getMatchID());
                var accepted = filter.isAcceptedMatch(new Match(match));
                if (!filter.isAutomatic() || (accepted && refresh)) {
                    OnlineWorker.downloadMatchData(match, refresh);
                }

                if (accepted) limit--;
                if (limit < 1) {
                    break;
                }
            }
        }

	    // Look for tournament matches if they are included in filter.
	    if (!filter.isAutomatic() || filter.isTournament()) {
		    // Current matches includes tournament matches
	    	matches = OnlineWorker.getMatches(teamId, true, false, false);
            if ( matches != null) {
                // newest first
                Collections.reverse(matches);

                // Only store tournament matches
                for (MatchKurzInfo match : matches) {
                    if (match.getMatchStatus() != MatchKurzInfo.FINISHED) {
                        continue;
                    }
                    if (filter.isAcceptedMatch(new Match(match))
                            && match.getMatchType().isTournament()
                            && DBManager.instance().matchLineupIsNotStored(match.getMatchType(), match.getMatchID())) {
                        OnlineWorker.downloadMatchData(match.getMatchID(), match.getMatchType(), false);
                    }
                }
            }
	    }
    }

    /**
     * Method that downloads from Hattrick the current players for the team
     * player values are aggregated to squad info which is stored separately to get historical trends of the squad development
     * @param teamId teamid to download players for
     */
    public static List<PlayerInfo> downloadPlayers(int teamId) {
        String xml;
        try {
            xml = MyConnector.instance().downloadPlayers(teamId);
        } catch (Exception e) {
            return null;
        }

        var lastMatchDate = HODateTime.HT_START;
        List<PlayerInfo> players = new ArrayList<>();
        var playerInfos = new XMLPlayersParser().parsePlayersFromString(xml);
        for (var i : playerInfos) {
            var player = new PlayerInfo(i);
            var matchDate =  player.getLastMatchDate();
            if ( matchDate != null && matchDate.isAfter(lastMatchDate)){
                lastMatchDate = matchDate;
            }
            players.add(player);
        }
        PlayerDataManager.update(players);

        if ( lastMatchDate.isAfter(HODateTime.HT_START) ) {
            var squadInfo = getSquadInfo(teamId, lastMatchDate, players);
            PlayerDataManager.update(squadInfo);
        }

        return players;
    }

    private static SquadInfo getSquadInfo(int teamId, HODateTime lastMatchDate, List<PlayerInfo> players) {
        var squadInfo = new SquadInfo(teamId, lastMatchDate);
        for (var player : players) {
            squadInfo.incrementPlayerCount();
            if (player.isTransferListed()) squadInfo.incrementTransferListedCount();
            if (player.getMotherClubBonus()) squadInfo.incrementHomegrownCount();

            squadInfo.addSalary(player.getSalary());
            squadInfo.addTsi(player.getTSI());
            var injuryLevel = player.getInjuryLevel();
            switch (injuryLevel) {
                case 0:
                    squadInfo.incrementBruisedCount();
                    break;
                case -1:
                    break;
                default:
                    squadInfo.addInjuredWeeksSum(injuryLevel);
                    squadInfo.incrementInjuredCount();
            }

            switch (player.getBookingStatus()) {
                case PlayerDataManager.YELLOW -> squadInfo.incrementSingleYellowCards();
                case PlayerDataManager.DOUBLE_YELLOW -> squadInfo.incrementTwoYellowCards();
                case PlayerDataManager.SUSPENDED -> squadInfo.incrementSuspended();
            }
        }
        return squadInfo;
    }

    public static Map<String, String> getTeamDetails(int teamId) {
        if (teamDetailsCache.containsKey(teamId)) {
            return teamDetailsCache.get(teamId);
        } else {
            Map<String, String> teamDetails = OnlineWorker.getTeam(teamId);
            teamDetailsCache.put(teamId, teamDetails);
            return teamDetails;
        }
    }

    /**
     * Downloads from Hattrick the team name
     *
     * @param teamId Teamid to download name for
     *
     * @return Team Name
     *
     */
    public static String downloadTeamName(int teamId) {
		String xml = MyConnector.instance().getHattrickXMLFile("/common/chppxml.axd?file=team&teamID=" + teamId);
        Document dom = XMLManager.parseString(xml);
        if ( dom != null) {
            Document teamDocument = dom.getElementsByTagName("Team").item(0).getOwnerDocument();
            return teamDocument.getElementsByTagName("TeamName").item(0).getFirstChild().getNodeValue();
        }
        return "";
    }

    public static TeamStats downloadSeriesDetails(int seriesId, int teamId) {
        Map<String, TeamStats> teamStatsMap;
        if (seriesDetailsCache.containsKey(seriesId)) {
            teamStatsMap = seriesDetailsCache.get(seriesId);
        } else {
            teamStatsMap = OnlineWorker.downloadLeagueDetails(seriesId);
        }

        if (teamStatsMap != null) {
            return  teamStatsMap.get(String.valueOf(teamId));
        } else {
            return null;
        }
    }

    /**
     * Check if CHPP rules approve download for a match
     *
     * @return true if allowed
     */
    public static boolean isDownloadAllowed() {

    	// CHPP-Teles confirms in staff message to bingeling (blaghaid) that this is not a problem
    	// We don't have to worry much about traffic anymore, but may want to check for new functionality.
    	// The team analyzer was discussed.

    	return true;
    }

}
