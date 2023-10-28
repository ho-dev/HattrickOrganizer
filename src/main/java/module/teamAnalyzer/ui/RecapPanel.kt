package module.teamAnalyzer.ui;

import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.ui.controller.RecapListSelectionListener;
import java.awt.*;
import java.io.Serial;
import javax.swing.*;


public class RecapPanel extends JPanel {

	@Serial
    private static final long serialVersionUID = 486150690031160261L;
    public static final String VALUE_NA = "---"; //$NON-NLS-1$

    //~ Instance fields ----------------------------------------------------------------------------
    private FixedColumnsTable table;

    private RecapListSelectionListener recapListener = null;

    private RecapPanelTableModel tableModel;
    /**
     * Creates a new RecapPanel object.
     */
    public RecapPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void reload(TeamReport teamReport) {
        this.tableModel.showTeamReport(teamReport);
    }

    private void jbInit() {
        tableModel = UserColumnController.instance().getTeamAnalyzerRecapModell();
        tableModel.showTeamReport(null);
        table = new FixedColumnsTable(2, tableModel);
        table.setDefaultRenderer(Object.class, new RecapTableRenderer());
        table.setDefaultRenderer(ImageIcon.class, new RecapTableRenderer());
        recapListener = new RecapListSelectionListener(table.getTableSorter(), tableModel);
        table.addListSelectionListener(recapListener);
        setLayout(new BorderLayout());
        add(table);
    }

    public String getSelectedTacticType() {
    	return recapListener.getSelectedTacticType();
    }

    public String getSelectedTacticSkill() {
    	return recapListener.getSelectedTacticSkill();
    }

    public void storeUserSettings() {
        this.tableModel.storeUserSettings(table);
    }
}
