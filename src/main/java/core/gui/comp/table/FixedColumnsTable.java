package core.gui.comp.table;

import module.transfer.ui.sorter.DefaultTableSorter;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

public class FixedColumnsTable extends JScrollPane {

    /**
     * Number of fixed columns in table
     */
    private final int fixedColumns;

    /**
     * Table sorter
     */
    private final DefaultTableSorter scrollTableSorter;

    /**
     * Fixed table part (left hand side)
     */
    private final JTable fixed;

    /**
     * Scrollable table part (right hand side)
     */
    private final JTable scroll;

    /**
     * Create a fixed columns table
     * Columns and Header tooltips are taken from table model.
     * Column settings are restored from database.
     * Internally two tables are created, "fixed" for the left hand side, "scroll" for the right hand side
     *
     * @param fixedColumns number of fixed columnms
     * @param tableModel table model
     */
    public FixedColumnsTable(int fixedColumns, HOTableModel tableModel) {
        this.fixedColumns = fixedColumns;

        scrollTableSorter = new DefaultTableSorter(tableModel);
        var table = new JTable(scrollTableSorter);
        setTooltipHeader(table, tableModel.getTooltips());

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.setViewportView(table);
        scroll = table;
        fixed = new JTable(scroll.getModel());
        fixed.setFocusable(false);
        fixed.setSelectionModel(scroll.getSelectionModel());
        fixed.getTableHeader().setReorderingAllowed(false);

        //  Remove the fixed columns from the main table
        int i=0;
        for (; i < fixedColumns; i++) {
            var _columnModel = scroll.getColumnModel();
            _columnModel.removeColumn(_columnModel.getColumn(0));
        }

        scroll.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fixed.setSelectionModel(scroll.getSelectionModel());

        //  Remove the non-fixed columns from the fixed table
        while (fixed.getColumnCount() > fixedColumns) {
            var _columnModel = fixed.getColumnModel();
            _columnModel.removeColumn(_columnModel.getColumn(fixedColumns));
        }

        //  Add the fixed table to the scroll pane
        fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
        setRowHeaderView(fixed);
        setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, fixed.getTableHeader());

        tableModel.restoreUserSettings(this);
    }

    private void setTooltipHeader(JTable table, String[] tooltips) {
        ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
        header.setToolTipStrings(tooltips);
        header.setToolTipText("");
        table.setTableHeader(header);
        scrollTableSorter.setTableHeader(table.getTableHeader());
    }


    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the Locked LeftTable
     *
     * @return Jtable
     */
    public JTable getFixedTable() {
        return fixed;
    }

    /**
     * Returns the Scrollable RightTable
     *
     * @return Jtable
     */
    public JTable getScrollTable() {
        return scroll;
    }

    /**
     * The provided renderer is set to both internal tables
     * @param columnClass  set the default cell renderer for this columnClass
     * @param renderer default cell renderer to be used for this columnClass
     */
    public void setDefaultRenderer(Class<?> columnClass, TableCellRenderer renderer) {
        this.fixed.setDefaultRenderer(columnClass, renderer);
        this.scroll.setDefaultRenderer(columnClass, renderer);
    }

    /**
     * Add a list selection listener
     * @param listener
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        ListSelectionModel rowSM = scroll.getSelectionModel();
        rowSM.addListSelectionListener(listener);
    }

    /**
     * Return the created table sorter
     * @return DefaultTableSorter
     */
    public DefaultTableSorter getTableSorter() {
        return scrollTableSorter;
    }

    /**
     * Return the number of fixed columns
     * @return int
     */
    public int getFixedColumnsCount() {
        return fixedColumns;
    }
}
