package module.teamanalyzer.ui;

import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import core.util.HODateTime;
import module.teamanalyzer.SystemManager;
import module.teamanalyzer.report.TeamReport;
import java.awt.*;
import javax.swing.*;


public class RecapPanel extends JPanel {

    public static final String VALUE_NA = "---"; //$NON-NLS-1$
    private String selectedTacticSkill;
    private String selectedTacticType;

    private final RecapPanelTableModel tableModel;

    /**
     * Creates a new RecapPanel object.
     */
    public RecapPanel() {
        tableModel = UserColumnController.instance().getTeamAnalyzerRecapModel();
        tableModel.showTeamReport(null);
        //~ Instance fields ----------------------------------------------------------------------------
        FixedColumnsTable table = new FixedColumnsTable(tableModel, 2);
        tableModel.initTable(table);
        table.setDefaultRenderer(Object.class, new RecapTableRenderer());
        table.setDefaultRenderer(ImageIcon.class, new RecapTableRenderer());
        table.addListSelectionListener(e->{
            if (e.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (!lsm.isSelectionEmpty()) {
                selectedTacticType = tableModel.getTacticType(lsm.getMinSelectionIndex());
                selectedTacticSkill = tableModel.getTacticSkill(lsm.getMinSelectionIndex());

                var lineup = tableModel.getTeamMatchReport(lsm.getMinSelectionIndex());
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
        });
        setLayout(new BorderLayout());
        add(table.getContainerComponent());
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void reload(TeamReport teamReport) {
        this.tableModel.showTeamReport(teamReport);
    }

    public String getSelectedTacticType() {
    	return selectedTacticType;
    }

    public String getSelectedTacticSkill() {
    	return selectedTacticSkill;
    }

    public void storeUserSettings() {
        this.tableModel.storeUserSettings();
    }
}
