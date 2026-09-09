package module.teamanalyzer.ui;

import core.gui.model.BaseTableModel;
import core.model.player.MatchRoleID;
import module.teamanalyzer.report.TacticReport;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

public class TacticPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------

	private DefaultTableModel tableModel;
    private JTable table;
    private NumberFormat f = new DecimalFormat("#.#");

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TacticPanel object.
     */
    public TacticPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public String format(double number) {
        return f.format(number);
    }

    public void reload(List<TacticReport> list) {
        tableModel = new BaseTableModel(new Vector<>(),
                                        new Vector<>(Arrays.asList(new String[]{
                                                                     "COL_A", "COL_B", "COL_C"
                                                                 })));
        table.setModel(tableModel);

        Vector<Object> rowData;
        int row = 0;

        for (TacticReport report : list) {
            rowData = new Vector<>();
            rowData.add(MatchRoleID.getNameForPosition((byte) report.getTacticCode()));
            rowData.add("" + report.getAppearance());
            rowData.add(format(report.getRating()));
            tableModel.addRow(rowData);
            row++;

            if (row == 3) {
                break;
            }
        }

        if (row > 0) {
            this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

            for (; row < 3; row++) {
                tableModel.addRow(emptyLine());
            }
        } else {
            this.setBorder(null);
        }

        table.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(30);
        table.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(30);
    }

    /**
     *
     */
    private Vector<Object> emptyLine() {
        Vector<Object> v = new Vector<>();

        return v;
    }

    private void jbInit() {
        Vector<Vector<Object>> data = new Vector<>();

        tableModel = new BaseTableModel(data,
                                        new Vector<>(Arrays.asList(new String[]{
                                                                     "COL_A", "COL_B", "COL_C"
                                                                 })));
        table = new JTable(tableModel);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        this.setLayout(new BorderLayout());
        this.add(table, BorderLayout.CENTER);
        table.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(30);
        table.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(30);
    }
}
