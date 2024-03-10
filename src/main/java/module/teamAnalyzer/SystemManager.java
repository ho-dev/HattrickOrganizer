package module.teamAnalyzer;

import core.model.match.IMatchDetails;
import core.module.config.ModuleConfig;
import core.prediction.engine.TeamData;
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
import java.util.List;

/**
 * This is a class where all the relevant and shared plugin info are kept
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class SystemManager {
	private final static String ISNUMERICRATING = "TA_numericRating";
	private final static String ISLINEUP = "TA_lineupCompare";
	private final static String ISTACTICDETAIL = "TA_tacticDetail";
	private final static String ISDESCRIPTIONRATING = "TA_descriptionRating";
	private final static String ISSHOWUNAVAILABLE = "TA_isShowUnavailable";
	private final static String ISMIXEDLINEUP = "TA_mixedLineup";
	private final static String ISSHOWPLAYERINFO = "TA_isShowPlayerInfo";
	private final static String ISCHECKTEAMNAME = "TA_isCheckTeamName";

	public static class Setting {
		Boolean is;
		boolean def;
		String configKey;

		public Setting(String configKey){
			this(configKey,true);
		}

		public Setting(String configKey, boolean def){
			this.def = def;
			this.configKey=configKey;
		}

		public boolean isSet(){
			if ( is == null){
				is = ModuleConfig.instance().getBoolean(configKey);
				if ( is == null) is = def; // default
			}
			return is;
		}

		public void set(boolean selected) {
			is = selected;
			ModuleConfig.instance().setBoolean(configKey,is);
		}
	}

	public static Setting isNumericRating = new Setting(ISNUMERICRATING, false);
	public static Setting isLineup = new Setting(ISLINEUP);
	public static Setting isTacticDetail = new Setting(ISTACTICDETAIL, false);
	public static Setting isDescriptionRating = new Setting(ISDESCRIPTIONRATING);
	public static Setting isShowUnavailable = new Setting(ISSHOWUNAVAILABLE);
	public static Setting isMixedLineup = new Setting(ISMIXEDLINEUP, false);
	public static Setting isShowPlayerInfo = new Setting(ISSHOWPLAYERINFO, false);
	public static Setting isCheckTeamName = new Setting(ISCHECKTEAMNAME);

	/**
	 * The Selected Team
	 */
	private static Team selectedTeam;

	/** Boolean for the updating process being ongoing */
	private static boolean updating = false;

	/** Reference to the plugin itself */
	private static TeamAnalyzerPanel plugin;

	public static int getSelectedTeamLocation() {
		return selectedTeamLocation;
	}

	public static void setSelectedTeamLocation(int selectedTeamLocation) {
		SystemManager.selectedTeamLocation = selectedTeamLocation;
	}

	private static int selectedTeamLocation;

	/**
	 * Set the active team, i.e. the team currently selected in the drop-down.
	 *
	 * @param team
	 */
	public static void setActiveTeam(Team team) {
		selectedTeam = team;
		if (selectedTeam != null) {
			setSelectedTeamLocation((selectedTeam.isHomeMatch()) ? IMatchDetails.LOCATION_HOME : IMatchDetails.LOCATION_AWAY);
		}
	}

	/**
	 * Get the active team ID
	 *
	 * @return int
	 */
	public static int getActiveTeamId() {
		if (selectedTeam == null) {
			selectedTeam = TeamManager.getFirstTeam();
		}
		return selectedTeam.getTeamId();
	}

	/**
	 * Get the active team Name
	 *
	 * @return String
	 */
	public static String getActiveTeamName() {
		return selectedTeam.getName();
	}

	/**
	 * Returns the main Plugin class
	 *
	 * @return TeamAnalyzerPanel
	 */
	public static TeamAnalyzerPanel getPlugin() {
		return plugin;
	}

	/**
	 * Initialize the instance
	 *
	 * @param aPlugin
	 *            main plugin class
	 */
	public static void initialize(TeamAnalyzerPanel aPlugin) {
		plugin = aPlugin;
		setActiveTeam(TeamManager.getFirstTeam());
	}

	/**
	 * Refresh all the plugins data after an event
	 */
	public static void refresh() {
		if (plugin != null) {
			NameManager.clean();
			TeamAnalyzerPanel.filter.setMatches(new ArrayList<>());

			teamReport = null;
			MatchPopulator.clean();
			MatchManager.clean();
			plugin.getMainPanel().reload(null, 0, 0);
			updateUI();
		}
	}

	/**
	 * Refresh only the data without recalculating everything
	 */
	public static void refreshData() {
		if (!updating) {
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
		if (!MatchPopulator.getAnalyzedMatch().isEmpty()) {
			teamReport = new TeamReport(getActiveTeamId(), matchDetails);
		} else {
			teamReport = null;
		}
		List<String> filterList = new ArrayList<>();
		for (Match match : MatchManager.getSelectedMatches()) {
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
			teamReport = new TeamReport(getActiveTeamId(), new ArrayList<>()); // create an empty team report
		}
		return teamReport;
	}

	public static void adjustRatingsLineup(TeamData newRatings) {
		getTeamReport().adjustRatingsLineup(newRatings);
		updateUI();
	}
}
