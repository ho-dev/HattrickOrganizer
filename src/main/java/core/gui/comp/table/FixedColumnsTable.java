package core.gui.comp.table;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import module.transfer.ui.sorter.DefaultTableSorter;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class FixedColumnsTable extends JScrollPane {

    /**
     * Number of fixed columns in table
     */
    private final int fixedColumns;

    /**
     * Table sorter
     */
    private DefaultTableSorter scrollTableSorter;

    /**
     * Fixed table part (left hand side)
     */
    private JTable fixed;

    /**
     * Scrollable table part (right hand side)
     */
    private JTable scroll;

    /**
     * Create a fixed columns table
     * Columns and Header tooltips are taken from table model.
     * Column settings are restored from database.
     * Internally two tables are created, "fixed" for the left hand side, "scroll" for the right hand side
     *
     * @param fixedColumns number of fixed columns
     */
    public FixedColumnsTable(int fixedColumns) {
        this.fixedColumns = fixedColumns;
    }

    public void setTableModel(HOTableModel tableModel) {

        scrollTableSorter = new DefaultTableSorter(tableModel);
        var table = new JTable(scrollTableSorter);
        setTooltipHeader(table, tableModel.getTooltips());

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);

        this.setViewportView(table);
        scroll = table;

        for (int i=0; i<scroll.getColumnCount(); i++){
            var tm = tableModel.columns[i];
            var cm = scroll.getColumnModel().getColumn(i);
            cm.setMinWidth(tm.minWidth);
        }

        fixed = new JTable(scroll.getModel());
        fixed.setFocusable(false);
        fixed.setSelectionModel(scroll.getSelectionModel());
        fixed.getTableHeader().setReorderingAllowed(false);


        //  Remove the fixed columns from the main table
        int width = 0;
        int i=0;
        for (; i < fixedColumns; i++) {
            var _columnModel = scroll.getColumnModel();
            var column = _columnModel.getColumn(0);
            width += column.getMinWidth();
            _columnModel.removeColumn(column);
        }

        scroll.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fixed.setSelectionModel(scroll.getSelectionModel());

        //  Remove the non-fixed columns from the fixed table
        while (fixed.getColumnCount() > fixedColumns) {
            var _columnModel = fixed.getColumnModel();
            _columnModel.removeColumn(_columnModel.getColumn(fixedColumns));
        }

        //  Add the fixed table to the scroll pane
        fixed.setPreferredScrollableViewportSize(new Dimension(width, 0));
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
     * @return JTable
     */
    public JTable getFixedTable() {
        return fixed;
    }

    /**
     * Returns the Scrollable RightTable
     *
     * @return JTable
     */
    public JTable getScrollTable() {
        return scroll;
    }

    public void setRowSelectionInterval(int rowIndex0, int rowIndex1){
        fixed.setRowSelectionInterval(rowIndex0, rowIndex1);
        scroll.setRowSelectionInterval(rowIndex0, rowIndex1);
    }

    public int getSelectedRow(){
        return scroll.getSelectedRow();
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
     * @param listener ListSelectionListener
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        ListSelectionModel rowSM = scroll.getSelectionModel();
        rowSM.addListSelectionListener(listener);
    }

    public ListSelectionModel getSelectionModel() {return scroll.getSelectionModel();}
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
