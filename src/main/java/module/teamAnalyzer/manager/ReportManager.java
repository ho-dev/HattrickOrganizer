package module.teamAnalyzer.manager;

import core.module.config.ModuleConfig;
import core.prediction.engine.TeamData;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.ui.RecapPanel;
import module.teamAnalyzer.ui.TeamAnalyzerPanel;
import module.teamAnalyzer.vo.Match;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.Team;
import module.teamAnalyzer.vo.TeamLineup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/*
public class ReportManager {
    //~ Static fields/initializers -----------------------------------------------------------------
    // adjustedLineup is used by MatchPrediction to store MatchRatings adjusted by the user
    private static TeamLineup adjustedRatingsLineup;
    private static TeamLineup averageRatingslineup;
    private static List<MatchDetail> matchDetails;

    //~ Methods ------------------------------------------------------------------------------------
    public static TeamLineup getLineup(int gameNumber) {
        TeamReport report = new TeamReport();
        int i = 1;

        for (Iterator<MatchDetail> iter = matchDetails.iterator(); iter.hasNext();) {
            MatchDetail match = iter.next();

            if (i == gameNumber) {
                report.addMatch(match, true);

                break;
            }

            i++;
        }

        TeamLineupBuilder builder = new TeamLineupBuilder(report);

        return builder.getLineup();
    }

    public static TeamLineup getLineup() {
        return lineup;
    }
    public static TeamLineup getAdjustedLineup() {
        return adjustedLineup;
    }

    public static void buildReport(List<?> matchDetails) {
        TeamReport report = new TeamReport();

        for (Iterator<?> iter = matchDetails.iterator(); iter.hasNext();) {
            MatchDetail match = (MatchDetail) iter.next();

            report.addMatch(match, ModuleConfig.instance().getBoolean(SystemManager.ISSHOWUNAVAILABLE));
        }

        TeamLineupBuilder builder = new TeamLineupBuilder(report);

        lineup = builder.getLineup();
    }

    public static void clean() {
        lineup = null;
        matchDetails = new ArrayList<MatchDetail>();
    }

    public static void updateReport() {
        matchDetails = MatchManager.getMatchDetails();

        if (MatchPopulator.getAnalyzedMatch().size() > 0) {
            buildReport(matchDetails);
        } else {
            lineup = null;
        }

        updateFilteredMatches();
    }

    private static void updateFilteredMatches() {
        List<String> filterList = new ArrayList<String>();

        for (Iterator<Match> iter = MatchManager.getSelectedMatches().iterator(); iter.hasNext();) {
            Match match = iter.next();

            filterList.add("" + match.getMatchId());
        }

        TeamAnalyzerPanel.filter.setMatches(filterList);
    }

    public static void SetAdjustedLineup(TeamData opponentTeamData) {
        TeamLineupBuilder builder = new TeamLineupBuilder(lineup);
        adjustedLineup = builder.getLineup();

        adjustedLineup.getRating().setCentralAttack(opponentTeamData.getRatings().getMiddleAttack());
        adjustedLineup.getRating().setCentralDefense(opponentTeamData.getRatings().getMiddleDef());
        adjustedLineup.getRating().setLeftAttack(opponentTeamData.getRatings().getLeftAttack());
        adjustedLineup.getRating().setLeftDefense(opponentTeamData.getRatings().getLeftDef());
        adjustedLineup.getRating().setMidfield(opponentTeamData.getRatings().getMidfield());
        adjustedLineup.getRating().setRightAttack(opponentTeamData.getRatings().getRightAttack());
        adjustedLineup.getRating().setRightDefense(opponentTeamData.getRatings().getRightDef());

        SystemManager.updateUI();
    }

}
        */