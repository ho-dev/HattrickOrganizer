package module.transfer.ui.sorter;

import javax.swing.Icon;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.event.MouseListener;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TableSorter is a decorator for TableModels; adding sorting functionality to a supplied
 * TableModel. TableSorter does not store or copy the data in its TableModel; instead it maintains
 * a map from the row indexes of the view to the row indexes of the model. As requests are made of
 * the sorter (like getValueAt(row, col)) they are passed to the underlying model after the row
 * numbers have been translated via the internal mapping array. This way, the TableSorter appears
 * to hold another copy of the table with the rows in a different order.  TableSorter registers
 * itself as a listener to the underlying model, just as the JTable itself would. Events recieved
 * from the model are examined, sometimes manipulated (typically widened), and then passed on to
 * the TableSorter's listeners (typically the JTable). If a change to the model has invalidated
 * the order of TableSorter's rows, a note of this is made and the sorter will resort the rows the
 * next time a value is requested.  When the tableHeader property is set, either by using the
 * setTableHeader() method or the two argument constructor, the table header may be used as a
 * complete UI for TableSorter. The default renderer of the tableHeader is decorated with a
 * renderer that indicates the sorting status of each column. In addition, a mouse listener is
 * installed with the following behavior:   Mouse-click: Clears the sorting status of all other
 * columns and advances the sorting status of that column through three values: {NOT_SORTED,
 * ASCENDING, DESCENDING} (then back to NOT_SORTED again).  SHIFT-mouse-click: Clears the sorting
 * status of all other columns and cycles the sorting status of the column through the same three
 * values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.  CONTROL-mouse-click and
 * CONTROL-SHIFT-mouse-click: as above except that the changes to the column do not cancel the
 * statuses of columns that are already sorting - giving a way to initiate a compound sort.   This
 * is a long overdue rewrite of a class of the same name that first appeared in the swing table
 * demos in 1997.
 *
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @version 2.0 02/27/04
 */
public abstract class AbstractTableSorter extends AbstractTableModel {
	@Serial
    private static final long serialVersionUID = 1943995728912103888L;
    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    private static final Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);
    @SuppressWarnings("unchecked")
	public static final Comparator COMPARABLE_COMPARATOR = (o1, o2) -> ((Comparable) o1).compareTo(o2);
    public static final Comparator LEXICAL_COMPARATOR = (o1, o2) -> o1.toString().compareTo(o2.toString());

    protected TableModel tableModel;
    private JTableHeader tableHeader;
    private final List<Directive> sortingColumns = new ArrayList<>();
    private final Map<Class<?>,Comparator> columnComparators = new HashMap<>();
    private final MouseListener mouseListener;
    private final TableModelListener tableModelListener;
    private int[] modelToView;
    private Row[] viewToModel;

    /**
     * Creates a new TableSorter object.
     */
    public AbstractTableSorter() {
        this.mouseListener = new MouseHandler(this);
        this.tableModelListener = new TableModelHandler(this);
    }

    /**
     * Creates a new TableSorter object.
     *
     * @param tableModel Table model
     */
    public AbstractTableSorter(TableModel tableModel) {
        this();
        setTableModel(tableModel);
    }

    /**
     * Creates a new TableSorter object.
     *
     * @param tableModel Table model
     * @param tableHeader Header
     */
    public AbstractTableSorter(TableModel tableModel, JTableHeader tableHeader) {
        this();
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }

    @Override
	public boolean isCellEditable(int row, int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    @Override
	public Class<?> getColumnClass(int column) {
        return tableModel.getColumnClass(column);
    }

    public int getColumnCount() {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    @Override
	public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }

    public abstract Comparator getCustomComparator(int column);

    // TableModel interface methods 
    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    public boolean isSorting() {
        return !sortingColumns.isEmpty();
    }

    public List<Directive> getSortingColumns() {
        return sortingColumns;
    }

    public void setSortingStatus(int column, int status) {
        Directive directive = getDirective(column);

        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }

        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
        }

        sortingStatusChanged();
    }

    public int getSortingStatus(int column) {
        return getDirective(column).getDirection();
    }

    public void setTableHeader(JTableHeader tableHeader) {
        if (this.tableHeader != null) {
            this.tableHeader.removeMouseListener(mouseListener);

            TableCellRenderer defaultRenderer = this.tableHeader
                .getDefaultRenderer();

            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer)
                    .getTableCellRenderer());
            }
        }

        this.tableHeader = tableHeader;

        if (this.tableHeader != null) {
            this.tableHeader.addMouseListener(mouseListener);
            this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(
                    this, this.tableHeader.getDefaultRenderer()));
        }
    }

    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    public void setTableModel(TableModel tableModel) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;

        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    @Override
	public void setValueAt(Object aValue, int row, int column) {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(modelIndex(row), column);
    }

    public abstract boolean hasHeaderLine();

    public abstract int minSortableColumn();

    public int modelIndex(int viewIndex) {
        return getViewToModel()[viewIndex].getModelIndex();
    }

    protected Comparator getComparator(int column) {
        Comparator comparator = getCustomComparator(column);

        if (comparator != null) {
            return comparator;
        }

        Class<?> columnType = tableModel.getColumnClass(column);

        comparator = columnComparators.get(columnType);

        if (comparator != null) {
            return comparator;
        }

        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMPARATOR;
        }

        return LEXICAL_COMPARATOR;
    }

    protected Icon getHeaderRendererIcon(int column, int size) {
        Directive directive = getDirective(column);

        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }

        return new Arrow(directive.getDirection() == DESCENDING, size,
            sortingColumns.indexOf(directive));
    }

    protected void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    int[] getModelToView() {
        if (modelToView == null) {
            int n = getViewToModel().length;

            modelToView = new int[n];

            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }

        return modelToView;
    }

    void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    private Directive getDirective(int column) {
        for (Directive sortingColumn : sortingColumns) {

            if (((Directive) sortingColumn).getColumn() == column) {
                return (Directive) sortingColumn;
            }
        }

        return EMPTY_DIRECTIVE;
    }

    private Row[] getViewToModel() {
        if (viewToModel == null) {
            int tableModelRowCount = tableModel.getRowCount();

            viewToModel = new Row[tableModelRowCount];

            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(this, row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }

        return viewToModel;
    }

    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();

        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }
}
