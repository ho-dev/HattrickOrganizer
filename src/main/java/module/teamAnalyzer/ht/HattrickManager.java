// %1667190662:hoplugins.teamAnalyzer.ht%
package module.teamAnalyzer.ht;

import core.db.DBManager;
import core.file.xml.XMLManager;
import core.file.xml.XMLPlayersParser;
import core.model.match.MatchKurzInfo;
import core.net.MyConnector;
import core.net.OnlineWorker;
import core.util.HODateTime;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.Filter;
import module.teamAnalyzer.vo.Match;
import module.teamAnalyzer.vo.PlayerInfo;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Hattrick Download Helper class
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class HattrickManager {

    /**
     * Method that download from Hattrick the available matches for the team
     * If manual filter, the last 30 is made available.
     * If auto filter, enough matches to supply the filter needs are available.
     * Recent tournament are added if on manual, or if they are wanted, in addition to
     * the number specified.
     *
     * @param teamId teamid to download matches for
     * @param filter the match filter object.
     */
    public static void downloadMatches(final int teamId, Filter filter) {
   		int limit = Math.min(filter.getNumber(), 50);
   		
   		// If on manual, disable all filters, and download 30 matches.
   		if (!filter.isAutomatic()) {
   			limit = 30;
   		}

        var start = HODateTime.now().minus(8*30, ChronoUnit.DAYS);
	    List<MatchKurzInfo> matches = OnlineWorker.getMatchArchive( teamId, start, false);
        if ( matches != null) {
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
     * Method that download from Hattrick the current players for the team
     *
     * @param teamId teamid to download players for
     */
    public static List<PlayerInfo> downloadPlayers(int teamId) {
        String xml;
        try {
			xml = MyConnector.instance().downloadPlayers(teamId);
        } catch (Exception e) {
            return null;
        }

        List<PlayerInfo> players = new ArrayList<>();
        var playerInfos = new XMLPlayersParser().parsePlayersFromString(xml);
        for ( var i : playerInfos ) {
            players.add(new PlayerInfo(i));
        }

        PlayerDataManager.update(players);
        return players;
    }

    /**
     * Method that download from Hattrick the team name
     *
     * @param teamId Tteamid to download name for
     *
     * @return Team Name
     *
     */
    public static String downloadTeam(int teamId) {
		String xml = MyConnector.instance().getHattrickXMLFile("/common/chppxml.axd?file=team&teamID=" + teamId);
        Document dom = XMLManager.parseString(xml);
        if ( dom != null) {
            Document teamDocument = dom.getElementsByTagName("Team").item(0).getOwnerDocument();
            return teamDocument.getElementsByTagName("TeamName").item(0).getFirstChild().getNodeValue();
        }
        return "";
    }

    /**
     * Helper method to get a value from a Node.
     */
    private static int getIntValue(Node node, int i, String tag){
        return getIntValue(node, i, tag, 0);
    }
    private static int getIntValue(Node node, int i, String tag, int def) {
        try {
            String value = getValue(node, i, tag);
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }

        return def;
    }

    /**
     * Helper method to get a value from a Node.
     */
    private static String getValue(Node node, int i, String tag) {
        Node n = node.getOwnerDocument().getElementsByTagName(tag).item(i).getFirstChild();
        if ( n != null ){
            return n.getNodeValue();
        }
        return "";
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
