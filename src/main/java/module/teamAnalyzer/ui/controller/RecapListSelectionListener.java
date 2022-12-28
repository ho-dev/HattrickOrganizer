package module.teamAnalyzer.ui.controller;

import core.util.HODateTime;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.RecapPanel;
import module.teamAnalyzer.ui.RecapPanelTableModel;
import module.teamAnalyzer.vo.TeamLineup;
import module.transfer.ui.sorter.DefaultTableSorter;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Listener for the recap panel.
 *
 * @author draghetto, aik
 */
public class RecapListSelectionListener implements ListSelectionListener {

    private String selectedTacticType = RecapPanel.VALUE_NA;
    private String selectedTacticSkill = RecapPanel.VALUE_NA;
    private final DefaultTableSorter sorter;
    private final RecapPanelTableModel tableModel;

    /**
     * Consructor.
     */
    public RecapListSelectionListener(DefaultTableSorter sorter, RecapPanelTableModel tableModel) {
        this.sorter = sorter;
        this.tableModel = tableModel;
    }

    /**
     * Handle value changed events.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        if (!lsm.isSelectionEmpty()) {
            int selectedRow = sorter.modelIndex(lsm.getMinSelectionIndex());
            selectedTacticType = String.valueOf(tableModel.getValueAt(selectedRow, 17));
            selectedTacticSkill = String.valueOf(tableModel.getValueAt(selectedRow, 18));

            TeamLineup lineup = SystemManager.getTeamReport().selectLineup(selectedRow);
            int week = 0;
            int season = 0;
            if (lineup != null) {
                week = lineup.getWeek();
                season = lineup.getSeason();
                if (week < 0) {
                    var htdatetime = HODateTime.now().toLocaleHTWeek();
                    week = htdatetime.week;
                    season = htdatetime.season;
                }
            }
            SystemManager.getPlugin().getMainPanel().reload(lineup, week, season);
            SystemManager.getPlugin().getRatingPanel().reload(lineup);
            SystemManager.getPlugin().getSpecialEventsPanel().reload(lineup);
        }
    }


	/**
	 * Get the currently selected tactic type as i18ned string.
	 */
	public String getSelectedTacticType() {
		return selectedTacticType;
	}

	/**
	 * Get the skill of the currently selected tactic as i18ned string.
	 */
	public String getSelectedTacticSkill() {
		return selectedTacticSkill;
	}

}
