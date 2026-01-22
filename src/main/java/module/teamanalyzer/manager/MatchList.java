package module.teamanalyzer.manager;

import module.teamanalyzer.ui.TeamAnalyzerPanel;
import module.teamanalyzer.vo.Filter;
import module.teamanalyzer.vo.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchList {
    //~ Instance fields ----------------------------------------------------------------------------
    private final List<Match> matchList;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new MatchList object.
     */
    public MatchList() {
        matchList = new ArrayList<>();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public List<Match> getMatches() {
        return matchList;
    }

    public void addMatch(Match match) {
        matchList.add(match);
    }

    public List<Match> filterMatches(Filter filter) {
        int counter = 0;
        List<Match> list = new ArrayList<>();

        if (filter.isAutomatic()) {
            for (Match match : matchList) {
                if (TeamAnalyzerPanel.filter.isAcceptedMatch(match)) {
                    list.add(match);
                    counter++;
                }

                if (counter >= filter.getNumber()) {
                    break;
                }
            }
        } else {
            List<String> filterMatches = filter.getMatches();

            for (Match match : matchList) {
                if (filterMatches.contains("" + match.getMatchId())) {
                    list.add(match);
                }
            }
        }

        return list;
    }
}
