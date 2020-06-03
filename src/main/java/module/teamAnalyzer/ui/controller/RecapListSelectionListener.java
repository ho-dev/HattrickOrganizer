package module.teamAnalyzer.ui.controller;

import core.model.UserParameter;
import core.util.HTCalendarFactory;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.RecapPanel;
import module.teamAnalyzer.ui.RecapTableSorter;
import module.teamAnalyzer.ui.model.UiRecapTableModel;
import module.teamAnalyzer.vo.TeamLineup;

import java.util.Calendar;

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
    private RecapTableSorter sorter;
    private UiRecapTableModel tableModel;

    /**
     * Consructor.
     */
    public RecapListSelectionListener(RecapTableSorter sorter, UiRecapTableModel tableModel) {
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

            TeamLineup lineup = SystemManager.getTeamReport().getLineup(selectedRow);
            int week = 0;
            int season = 0;
            if (lineup != null) {
                week = lineup.getWeek();
                season = lineup.getSeason();
                if (week < 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR, UserParameter.instance().TimeZoneDifference);
                    week = HTCalendarFactory.getHTWeek(calendar.getTime());
                    season = HTCalendarFactory.getHTSeason(calendar.getTime());
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
