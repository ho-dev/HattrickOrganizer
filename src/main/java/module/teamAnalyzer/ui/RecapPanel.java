package module.teamAnalyzer.ui;

import core.gui.comp.table.ToolTipHeader;
import core.gui.model.UserColumnController;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.ui.controller.RecapListSelectionListener;
import module.transfer.ui.sorter.DefaultTableSorter;

import java.awt.*;
import java.io.Serial;
import javax.swing.*;


public class RecapPanel extends JPanel {

	@Serial
    private static final long serialVersionUID = 486150690031160261L;
    public static final String VALUE_NA = "---"; //$NON-NLS-1$

    //~ Instance fields ----------------------------------------------------------------------------
    private JTable table;
    //private UiRecapTableModel tableModel;
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
        DefaultTableSorter tableSorter = new DefaultTableSorter(tableModel);

        table = new JTable(tableSorter);
        ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
        header.setToolTipStrings(tableModel.getTooltips());
        header.setToolTipText("");
        table.setTableHeader(header);
        tableSorter.setTableHeader(table.getTableHeader());

        table.setDefaultRenderer(Object.class, new RecapTableRenderer());
        table.setDefaultRenderer(ImageIcon.class, new RecapTableRenderer());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        restoreUserSettings();

        ListSelectionModel rowSM = table.getSelectionModel();
        recapListener = new RecapListSelectionListener(tableSorter, tableModel);
        rowSM.addListSelectionListener(recapListener);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane);
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

    public void restoreUserSettings() {
        this.tableModel.restoreUserSettings(table);
    }
}
