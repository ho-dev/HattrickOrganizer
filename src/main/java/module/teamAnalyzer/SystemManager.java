// %2940960156:hoplugins.teamAnalyzer%
package module.teamAnalyzer;

import core.model.HOVerwaltung;
import module.teamAnalyzer.manager.MatchManager;
import module.teamAnalyzer.manager.MatchPopulator;
import module.teamAnalyzer.manager.NameManager;
import module.teamAnalyzer.manager.ReportManager;
import module.teamAnalyzer.manager.TeamManager;
import module.teamAnalyzer.ui.TeamAnalyzerPanel;
import module.teamAnalyzer.vo.Team;

import java.util.ArrayList;

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

	/** The Selected Team */
	private static Team selectedTeam;

	/** Boolean for the updating process being ongoing */
	private static boolean updating = false;

	/** Reference to the plugin itself */
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
			TeamAnalyzerPanel.filter.setMatches(new ArrayList<String>());

			ReportManager.clean();
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
			NameManager.clean();
			TeamManager.clean();
			refresh();
		}
	}

	/**
	 * Recalculate the report
	 */
	public static void updateReport() {
		updating = true;
		ReportManager.updateReport();
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
}
