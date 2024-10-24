package core.gui.comp.table;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOConfigurationIntParameter;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Table with fixed columns on the right hand side
 * The other columns can be sorted or disabled by the user
 */
public class FixedColumnsTable extends JTable {

    /**
     * Number of fixed columns in table
     */
    private final int fixedColumns;

    /**
     * Position of the divider between fixed and scrollable tables
     */
    private final HOConfigurationIntParameter dividerLocation;

    /**
     * Fixed table part (left hand side)
     */
    private final JTable fixed;

    /**
     * Container component for split pane of fixed and scrollable tables
     */
    private final JScrollPane scrollPane;


    /**
     * Constructor of table with one fixed columns
     * @param tableModel Table model
     */
    public FixedColumnsTable(HOTableModel tableModel) {
        this(tableModel, 1);
    }

    /**
     * Create a fixed columns table
     * Columns and Header tooltips are taken from table model.
     * Column settings are restored from database.
     * Internally two tables are created, "fixed" for the left hand side, "scroll" for the right hand side
     *
     * @param tableModel Table model
     * @param fixedColumns fixed columns count
     */
    public FixedColumnsTable(HOTableModel tableModel, int fixedColumns) {
        super(tableModel);
        this.fixedColumns = fixedColumns;
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
        fixed = new JTable(getModel());
        fixed.setFocusable(false);
        fixed.setSelectionModel(getSelectionModel());
        fixed.getTableHeader().setReorderingAllowed(false);

        //  Remove the fixed columns from the main table
        int width = 0;
        int i=0;
        for (; i < fixedColumns; i++) {
            var _columnModel = getColumnModel();
            var column = _columnModel.getColumn(0);
            width += column.getMinWidth();
            _columnModel.removeColumn(column);
        }

        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fixed.setSelectionModel(getSelectionModel());

        //  Remove the non-fixed columns from the fixed table
        while (fixed.getColumnCount() > fixedColumns) {
            var _columnModel = fixed.getColumnModel();
            _columnModel.removeColumn(_columnModel.getColumn(fixedColumns));
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(fixed), new JScrollPane(this));
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

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(splitPane);
    }

    /**
     * Returns the Locked left hand side table part
     *
     * @return JTable
     */
    public JTable getFixedTable() {
        return fixed;
    }

    /**
     * Set row selection interval of both tables synchronously
     * @param rowIndex0 one end of the interval
     * @param rowIndex1 the other end of the interval
     */
    public void setRowSelectionInterval(int rowIndex0, int rowIndex1){
        super.setRowSelectionInterval(rowIndex0, rowIndex1);
        if ( fixed != null ) fixed.setRowSelectionInterval(rowIndex0, rowIndex1);
    }

    /**
     * The provided renderer is set to both internal tables
     * @param columnClass  set the default cell renderer for this columnClass
     * @param renderer default cell renderer to be used for this columnClass
     */
    public void setDefaultRenderer(Class<?> columnClass, TableCellRenderer renderer) {
        super.setDefaultRenderer(columnClass, renderer);
        if ( fixed != null ) fixed.setDefaultRenderer(columnClass, renderer);
    }

    /**
     * Add a list selection listener
     * @param listener ListSelectionListener
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        ListSelectionModel rowSM = getSelectionModel();
        rowSM.addListSelectionListener(listener);
    }

    /**
     * Set the row sorter to both internal tables
     * @param sorter Sorter
     */
    public void setRowSorter(RowSorter<? extends TableModel> sorter) {
        super.setRowSorter(sorter);
        if ( fixed != null ) fixed.setRowSorter(sorter);
    }

    /**
     * Return the number of fixed columns
     * @return int
     */
    public int getFixedColumnsCount() {
        return fixedColumns;
    }

    /**
     * Returns the outer container component of the ficed column table
     * @return Component
     */
    public Component getContainerComponent() {
        return this.scrollPane;
    }
}
