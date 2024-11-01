package core.gui.comp.table;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOConfigurationIntParameter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.AdjustmentListener;

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
    private HOConfigurationIntParameter dividerLocation = null;

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

        final TableCellRenderer header = this.getTableHeader().getDefaultRenderer();
        this.getTableHeader().setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            Component tableCellRendererComponent = header.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            var tableColumn = table.getColumnModel().getColumn(column);
            var model = (HOTableModel) table.getModel();
            // Set header tool tip
            var tooltipString = model.getDisplayedColumns()[tableColumn.getModelIndex()].getTooltip();
            ((JComponent) tableCellRendererComponent).setToolTipText(tooltipString);
            return tableCellRendererComponent;
        });

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);

        if (fixedColumns > 0) {
            fixed = new JTable(getModel());
            fixed.setFocusable(false);
            fixed.setSelectionModel(getSelectionModel());
            fixed.getTableHeader().setReorderingAllowed(false);
            fixed.setSelectionModel(getSelectionModel());
            //  Remove the non-fixed columns from the fixed table
            while (fixed.getColumnCount() > fixedColumns) {
                var _columnModel = fixed.getColumnModel();
                _columnModel.removeColumn(_columnModel.getColumn(fixedColumns));
            }
            //  Remove the fixed columns from the main table
            int width = 0;
            int i = 0;
            for (; i < fixedColumns; i++) {
                var _columnModel = getColumnModel();
                var column = _columnModel.getColumn(0);
                width += column.getPreferredWidth();
                _columnModel.removeColumn(column);
            }

            // Sync scroll bars of both tables
            var fixedScrollPane = new JScrollPane(fixed);
            var rightScrollPane = new JScrollPane(this);
            fixedScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            rightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            final JScrollBar fixedScrollBar = fixedScrollPane.getVerticalScrollBar();
            final JScrollBar rightScrollBar = rightScrollPane.getVerticalScrollBar();

            // setVisible(false) does not have an effect, so we set the size to
            // false. We can't disable the scrollbar with VERTICAL_SCROLLBAR_NEVER
            // because this will disable mouse wheel scrolling.
            fixedScrollBar.setPreferredSize(new Dimension(0, 0));

            // Synchronize vertical scrolling
            AdjustmentListener adjustmentListener = e -> {
                if (e.getSource() == rightScrollBar) {
                    fixedScrollBar.setValue(e.getValue());
                } else {
                    rightScrollBar.setValue(e.getValue());
                }
            };
            fixedScrollBar.addAdjustmentListener(adjustmentListener);
            rightScrollBar.addAdjustmentListener(adjustmentListener);
            rightScrollPane.getVerticalScrollBar().setModel(fixedScrollPane.getVerticalScrollBar().getModel());
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fixedScrollPane, rightScrollPane);
            if (width == 0) width = 60;
            this.dividerLocation = new HOConfigurationIntParameter("TableDividerLocation_" + tableModel.getId(), width);
            splitPane.setDividerLocation(this.dividerLocation.getIntValue());
            splitPane.addPropertyChangeListener(evt -> {
                var propertyName = evt.getPropertyName();
                if (propertyName.equals("dividerLocation")) {
                    var pane = (JSplitPane) evt.getSource();
                    dividerLocation.setIntValue(pane.getDividerLocation());
                }
            });
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(splitPane);
        } else {
            fixed = null;
            scrollPane = new JScrollPane(this);
        }
    }

    /**
     * Returns the Locked left hand side table part
     *
     * @return JTable
     */
    public JTable getFixedTable() {
        return fixed;
    }

    public TableColumnModel getTableColumnModel() {
        return this.getColumnModel();
    }

    /**
     * Set row selection interval of both tables synchronously
     * @param rowIndex0 one end of the interval
     * @param rowIndex1 the other end of the interval
     */
    @Override
    public void setRowSelectionInterval(int rowIndex0, int rowIndex1){
        super.setRowSelectionInterval(rowIndex0, rowIndex1);
        if ( fixed != null ) fixed.setRowSelectionInterval(rowIndex0, rowIndex1);
    }

    /**
     * The provided renderer is set to both internal tables
     * @param columnClass  set the default cell renderer for this columnClass
     * @param renderer default cell renderer to be used for this columnClass
     */
    @Override
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
    @Override
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
     * Returns the outer container component of the fixed column table
     * @return Component
     */
    public Component getContainerComponent() {
        return this.scrollPane;
    }

    @Override
    public TableColumn getColumn(@NotNull Object identifier) {
        try {
            return super.getColumn(identifier);
        }
        catch( IllegalArgumentException e ) {
            return fixed.getColumn(identifier);
        }
    }
    public TableColumn getTableColumn(int i) {
        if (i<fixedColumns) {return fixed.getColumnModel().getColumn(i);}
        return super.getColumnModel().getColumn(i-fixedColumns);
    }
}
