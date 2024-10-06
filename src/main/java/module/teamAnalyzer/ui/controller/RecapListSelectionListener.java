package module.teamAnalyzer.ui.controller;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.table.HOTableModel;
import core.util.HODateTime;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.RecapPanel;
import module.teamAnalyzer.ui.RecapPanelTableModel;
import module.teamAnalyzer.vo.TeamLineup;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;


/**
 * Listener for the recap panel.
 *
 * @author draghetto, aik
 */
public class RecapListSelectionListener implements ListSelectionListener {

    private String selectedTacticType = RecapPanel.VALUE_NA;
    private String selectedTacticSkill = RecapPanel.VALUE_NA;
    private final TableRowSorter<HOTableModel> sorter;
    private final RecapPanelTableModel tableModel;

    /**
     * Consructor.
     */
    public RecapListSelectionListener(TableRowSorter<HOTableModel> sorter, RecapPanelTableModel tableModel) {
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
            int selectedRow = sorter.convertRowIndexToModel(lsm.getMinSelectionIndex());
            var colorLabelEntry = (ColorLabelEntry)tableModel.getValueAt(selectedRow, 17);
            if ( colorLabelEntry != null ) selectedTacticType = colorLabelEntry.getText();
            colorLabelEntry = (ColorLabelEntry)tableModel.getValueAt(selectedRow,18);
            if ( colorLabelEntry != null ) selectedTacticSkill = colorLabelEntry.getText();

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
