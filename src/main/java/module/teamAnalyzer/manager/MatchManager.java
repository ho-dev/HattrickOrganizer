package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.match.MatchKurzInfo;
import core.module.config.ModuleConfig;
import module.matches.SpielePanel;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.TeamAnalyzerPanel;
import module.teamAnalyzer.vo.Match;
import module.teamAnalyzer.vo.MatchDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class MatchManager {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static MatchList matches = null;

    //~ Methods ------------------------------------------------------------------------------------
    public static List<Match> getAllMatches() {
        return matches.getMatches();
    }

    public static List<MatchDetail> getMatchDetails() {
        List<Match> filteredMatches = getSelectedMatches();
        MatchPopulator matchPopulator = new MatchPopulator();

        return matchPopulator.populate(filteredMatches);
    }

    public static List<Match> getSelectedMatches() {
        if (matches == null) {
            loadActiveTeamMatchList();
        }

        return matches.filterMatches(TeamAnalyzerPanel.filter);
    }

    public static void clean() {
        loadActiveTeamMatchList();
    }

    public static void loadActiveTeamMatchList() {
        matches = new MatchList();

        SortedSet<Match> sortedMatches = loadMatchList();

        for (Iterator<Match> iter = sortedMatches.iterator(); iter.hasNext();) {
            Match element = iter.next();

            matches.addMatch(element);
        }
    }

    private static List<Match> getTeamMatch() {
        List<Match> teamMatches = new ArrayList<Match>();
        String oldName = SystemManager.getActiveTeamName();

        MatchKurzInfo[] matchKurtzInfo = DBManager.instance().getMatchesKurzInfo(SystemManager
                                                                                .getActiveTeamId(),
                                                                                SpielePanel.NUR_EIGENE_SPIELE,
                                                                                false);

        for (int i = 0; i < matchKurtzInfo.length; i++) {
            MatchKurzInfo matchInfo = matchKurtzInfo[i];

            if (matchInfo.getMatchStatus() != MatchKurzInfo.FINISHED) {
                continue;
            }

            Match match = new Match(matchInfo);

            String temp;

            if (match.getHomeId() == SystemManager.getActiveTeamId()) {
                temp = match.getHomeTeam();
            } else {
                temp = match.getAwayTeam();
            }

            if (ModuleConfig.instance().getBoolean(SystemManager.ISCHECKTEAMNAME)) {
                // Fix for missing last dot!
                String oldShort = oldName.substring(0, oldName.length() - 1);

                if (oldShort.equalsIgnoreCase(temp)) {
                    temp = oldName;
                }

                if (!temp.equalsIgnoreCase(oldName)) {
                    /* Team name can be changed the name between seasons
                     * without being bot in between. Team name can be changed
                     * after the 14th league game which makes that game the
                     * last possible match to hold old name. 
                     */
                    if (match.getWeek() > 13) {
                        oldName = temp;
                    } else {
                        return teamMatches;
                    }
                }
            }

            teamMatches.add(match);
        }

        return teamMatches;
    }

    private static SortedSet<Match> loadMatchList() {
        Map<String,Match> matchIds = new HashMap<String,Match>();

        for (Iterator<Match> iter = getTeamMatch().iterator(); iter.hasNext();) {
            Match match = iter.next();

            if (!matchIds.containsKey(match.getMatchId() + "")) {
                matchIds.put(match.getMatchId() + "", match);
            }
        }

        Collection<Match> matchList = matchIds.values();
        SortedSet<Match> sorted = getSortedSet(matchList, new MatchComparator());

        return sorted;
    }
    
    private static<T> SortedSet<T> getSortedSet(Collection<T> beans, Comparator<T> comparator) {
        final SortedSet<T> set = new TreeSet<T>(comparator);

        if ((beans != null) && (beans.size() > 0)) {
            set.addAll(beans);
        }

        return set;
    }
}
