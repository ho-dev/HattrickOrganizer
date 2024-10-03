package core.gui.comp.table;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOConfigurationIntParameter;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public class FixedColumnsTable extends JScrollPane {

    /**
     * Number of fixed columns in table
     */
    private final int fixedColumns;

    private final HOConfigurationIntParameter dividerLocation;

    /**
     * Table sorter
     */
    private final TableRowSorter<HOTableModel> tableRowSorter;

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
     * @param fixedColumns number of fixed columns
     */
    public FixedColumnsTable(HOTableModel tableModel, int fixedColumns) {
        this.fixedColumns = fixedColumns;
        tableRowSorter = new TableRowSorter<>(tableModel);
        var table = new JTable(tableModel);

        var columnModel = table.getColumnModel();
        ToolTipHeader header = new ToolTipHeader(columnModel);
        header.setToolTipStrings(tableModel.getTooltips());
        header.setToolTipText("");
        table.setTableHeader(header);

        table.setRowSorter(tableRowSorter);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
        scroll = table;
        for (int i=0; i<scroll.getColumnCount(); i++){
            var tm = tableModel.columns[i];
            var cm = scroll.getColumnModel().getColumn(i);
            cm.setIdentifier(tm.getId());
            cm.setMinWidth(tm.minWidth);
        }
        fixed = new JTable(scroll.getModel());
        fixed.setFocusable(false);
        fixed.setSelectionModel(scroll.getSelectionModel());
        fixed.setRowSorter(scroll.getRowSorter());
        fixed.getTableHeader().setReorderingAllowed(false);

        //  Remove the fixed columns from the main table
        int width = 0;
        int i=0;
        for (; i < fixedColumns; i++) {
            var tm = tableModel.columns[i];
            var cm = fixed.getColumnModel().getColumn(i);
            cm.setIdentifier(tm.getId());   // identifier has to be resetted

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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(fixed), new JScrollPane(scroll));


        if ( width == 0) width = 60;
        this.dividerLocation = new HOConfigurationIntParameter("TableDividerLocation_" + tableModel.getId(), width);
        splitPane.setDividerLocation(this.dividerLocation.getIntValue());
        splitPane.addPropertyChangeListener(evt -> {
            var propertyName = evt.getPropertyName();
            if (propertyName.equals("dividerLocation")) {
                var pane = (JSplitPane)evt.getSource();
                dividerLocation.setIntValue(pane.getDividerLocation());
            }
        });

        setViewportView(splitPane);
    }

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
    public TableRowSorter<HOTableModel> getTableRowSorter() {
        return tableRowSorter;
    }

    /**
     * Return the number of fixed columns
     * @return int
     */
    public int getFixedColumnsCount() {
        return fixedColumns;
    }

}
