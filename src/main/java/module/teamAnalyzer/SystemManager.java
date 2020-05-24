// %2940960156:hoplugins.teamAnalyzer%
package module.teamAnalyzer;

import core.model.HOVerwaltung;
import module.teamAnalyzer.manager.MatchManager;
import module.teamAnalyzer.manager.MatchPopulator;
import module.teamAnalyzer.manager.NameManager;
import module.teamAnalyzer.manager.TeamManager;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.ui.TeamAnalyzerPanel;
import module.teamAnalyzer.vo.Match;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a class where all the relevant and shared plugin info are kept
 * 
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class SystemManager {
	public final static String ISNUMERICRATING = "TA_numericRating";
	public final static String ISLINEUP = "TA_lineupCompare";
	public final static String ISTACTICDETAIL = "TA_tacticDetail";
	public final static String ISDESCRIPTIONRATING = "TA_descriptionRating";
	public final static String ISSHOWUNAVAILABLE = "TA_isShowPlayerInfo";
	public final static String ISMIXEDLINEUP = "TA_mixedLineup";
	public final static String ISSTARS = "TA_isStars";
	public final static String ISTOTALSTRENGTH = "TA_isTotalStrength";
	public final static String ISSQUAD = "TA_isSquad";
	public final static String ISSMARTSQUAD = "TA_isSmartSquad";
	public final static String ISLODDARSTATS = "TA_isLoddarStats";
	public final static String ISSHOWPLAYERINFO = "TA_isShowPlayerInfo";
	public final static String ISCHECKTEAMNAME = "TA_isCheckTeamName";

	/**
	 * The Selected Team
	 */
	private static Team selectedTeam;

	/**
	 * The next league opponent team
	 */
	private static Team leagueOpponent;

	/**
	 * The next cup/friendly opponent team
	 */
	private static Team cupOpponent;

	/**
	 * The next tournament opponent team
	 */
	private static Team tournamentOpponent;

	/**
	 * Boolean for the updating process being ongoing
	 */
	private static boolean updating = false;

	/**
	 * Reference to the plugin itself
	 */
	private static TeamAnalyzerPanel plugin;

	/**
	 * Set the active team
	 *
	 * @param team
	 */
	public static void setActiveTeam(Team team) {
		selectedTeam = team;
	}

	/**
	 * Get the active team ID
	 *
	 * @return
	 */
	public static int getActiveTeamId() {
		return selectedTeam.getTeamId();
	}

	/**
	 * Get the active team Name
	 *
	 * @return
	 */
	public static String getActiveTeamName() {
		return selectedTeam.getName();
	}

	/**
	 * Get next cup/friendly opponent team Id
	 *
	 * @return
	 */
	public static int getCupOpponentId() {
		return cupOpponent.getTeamId();
	}

	/**
	 * Get next league opponent team Id
	 *
	 * @return
	 */
	public static int getLeagueOpponentId() {
		return leagueOpponent.getTeamId();
	}

	/**
	 * Get next tournament opponent team Id
	 *
	 * @return
	 */
	public static int getTournamentOpponentId() {
		return tournamentOpponent.getTeamId();
	}

	/**
	 * Returns the main Plugin class
	 *
	 * @return
	 */
	public static TeamAnalyzerPanel getPlugin() {
		return plugin;
	}

	/**
	 * Initialize the instance
	 *
	 * @param aPlugin main plugin class
	 */
	public static void initialize(TeamAnalyzerPanel aPlugin) {
		plugin = aPlugin;
		leagueOpponent = TeamManager.getNextLeagueOpponent();
		cupOpponent = TeamManager.getNextCupOpponent();
		tournamentOpponent = TeamManager.getNextTournamentOpponent();

		if (leagueOpponent.getTeamId() == 0 && cupOpponent.getTeamId() == 0
				&& tournamentOpponent.getTeamId() == 0) {
			Team team = new Team();

			team.setName(HOVerwaltung.instance().getModel().getBasics().getTeamName());
			team.setTeamId(HOVerwaltung.instance().getModel().getBasics().getTeamId());
			setActiveTeam(team);
		} else if (leagueOpponent.isBefore(cupOpponent)
				&& leagueOpponent.isBefore(tournamentOpponent)) {
			// League is the next match
			setActiveTeam(leagueOpponent);
		} else if (cupOpponent.isBefore(tournamentOpponent)) {
			setActiveTeam(cupOpponent);
		} else {
			setActiveTeam(tournamentOpponent);
		}
	}

	/**
	 * Refresh all the plugins data after an event
	 */
	public static void refresh() {
		if (plugin != null) {
			NameManager.clean();
			TeamAnalyzerPanel.filter.setMatches(new ArrayList<String>());

			teamReport = null; //ReportManager.clean();
			MatchPopulator.clean();
			MatchManager.clean();
			plugin.getMainPanel().reload(null, 0, 0);
			updateUI();
		}
	}

	/**
	 * Refrwsh only the data without recalculating everything
	 */
	public static void refreshData() {
		if (!updating) {
			leagueOpponent = TeamManager.getNextLeagueOpponent();
			cupOpponent = TeamManager.getNextCupOpponent();
			tournamentOpponent = TeamManager.getNextTournamentOpponent();
			NameManager.clean();
			TeamManager.clean();
			refresh();
		}
	}

	private static TeamReport teamReport;

	/**
	 * Recalculate the report
	 */
	public static void updateReport() {
		updating = true;
		List<MatchDetail> matchDetails = MatchManager.getMatchDetails();
		if (MatchPopulator.getAnalyzedMatch().size() > 0) {
			teamReport = new TeamReport(matchDetails);
		} else {
			teamReport = null;
		}
		List<String> filterList = new ArrayList<String>();
		for (Iterator<Match> iter = MatchManager.getSelectedMatches().iterator(); iter.hasNext(); ) {
			Match match = iter.next();

			filterList.add("" + match.getMatchId());
		}
		TeamAnalyzerPanel.filter.setMatches(filterList);
		updating = false;
		updateUI();
	}

	/**
	 * Update the UI
	 */
	public static void updateUI() {
		if (plugin != null) {
			// plugin is null if options are visited before team analyzer is
			// accessed (lazy loading).
			// And the options calls this function after modification.
			plugin.reload();
		}
	}

	public static TeamReport getTeamReport() {
		if ( teamReport == null){
			teamReport = new TeamReport(new ArrayList<>()); // create an empty team report
		}
		return teamReport;
	}
}
