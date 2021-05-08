package module.youth;

// Adapted from http://www.java2s.com/Code/Java/Swing-JFC/TableSorterextendsAbstractTableModel.htm

import core.gui.comp.table.HOTableModel;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import static javax.swing.event.TableModelEvent.ALL_COLUMNS;


public class YouthTableSorter extends AbstractTableModel {

    protected HOTableModel tableModel;
    protected JTable table;

    enum Order {
        descending(-1),
        none(0),
        ascending(1);

        private int value;

        Order(int value) {
            this.value = value;
        }

        public static Order valueOf(int order) {
            if (order == 1) {
                return ascending;
            } else if (order == -1) {
                return descending;
            }
            return none;
        }

        public int getValue() {
            return value;
        }
    }

    private Row[] viewToModel;
    private int[] modelToView;
    private List<ColumnSorting> sortingColumns = new ArrayList<>();

    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    public HOTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(HOTableModel tableModel) {
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

    private void setTableHeader(JTableHeader tableHeader) {
        if (tableHeader.getComponentListeners().length > 0) return;// Listener already present.
        tableHeader.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTableHeader h = (JTableHeader) e.getSource();
                TableColumnModel columnModel = h.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = columnModel.getColumn(viewColumn).getModelIndex();
                if (column != -1) {
                    var selection = getSelectedModelIndex();
                    var status = getSortingOrder(column).getValue();
                    if (!e.isControlDown()) {
                        cancelSorting();
                    }
                    // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
                    // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
                    status = status + (e.isShiftDown() ? -1 : 1);
                    status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
                    setSortingStatus(column, Order.valueOf(status));
                    setSelectedModelIndex(selection);
                }
            }
        });
    }

    public void setSelectedModelIndex(int modelIndex) {
        if ( modelIndex > -1){
            modelIndex = viewIndex(modelIndex);
            table.setRowSelectionInterval(modelIndex, modelIndex);
        }
    }

    public int getSelectedModelIndex() {
        var selection = table.getSelectedRow();
        if ( selection > -1){
            selection = modelIndex(selection);
        }
        return selection;
    }

    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    private ColumnSorting getColumnSorting(int column) {
        return sortingColumns.stream().filter(i -> i.column == column).findFirst().orElse(null);
    }

    public Order getSortingOrder(int column) {
        var columnSorting = getColumnSorting(column);
        if (columnSorting != null) return columnSorting.order;
        return Order.none;
    }

    private void sortingStatusChanged() {
        clearSortingState();
        sort();
    }

    public void setSortingStatus(int column, Order status) {
        var columnSorting = getColumnSorting(column);
        if (columnSorting != null) {
            sortingColumns.remove(columnSorting);
        }
        if (status != Order.none) {
            sortingColumns.add(new ColumnSorting(column, status));
        }
        sortingStatusChanged();
    }

    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    private Row[] getViewToModel() {
        if (viewToModel == null) {
            int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];
            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }
        return viewToModel;
    }

    public int modelIndex(int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    public int viewIndex(int modelIndex){
        return getModelToView()[modelIndex];
    }

    private int[] getModelToView() {
        if (modelToView == null) {
            int n = getViewToModel().length;
            modelToView = new int[n];
            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }
        return modelToView;
    }

    // TableModel interface methods

    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    public int getColumnCount() {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }

    public boolean isCellEditable(int row, int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(modelIndex(row), column);
    }

    public void setValueAt(Object aValue, int row, int column) {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    // Helper classes

    private class Row implements Comparable {
        private int modelIndex;

        public Row(int index) {
            this.modelIndex = index;
        }

        public int compareTo(@NotNull Object o) {
            int row1 = modelIndex;
            int row2 = ((Row) o).modelIndex;

            for (var columnSorting : sortingColumns) {
                int column = columnSorting.column;
                Object o1 = tableModel.getValueAt(row1, column);
                Object o2 = tableModel.getValueAt(row2, column);

                if (o1 == null && o2 == null) {
                    continue;   // check next column, if any. Else return 0!
                } else if (o1 == null) {
                    return columnSorting.getComparison(-1);
                } else if (o2 == null) {
                    return columnSorting.getComparison(1);
                }

                var comparable = (Comparable<Object>) o1;
                var compare = comparable.compareTo(o2);
                if (compare != 0) {
                    return columnSorting.getComparison(compare);
                }
            }
            return 0;
        }
    }

    private TableModelListener tableModelListener = new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
            // If we're not sorting by anything, just pass the event along.
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);
                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);
                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if modelToView
            // is already allocated. If we don't do this check; sorting can become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            int column = e.getColumn();
            if (e.getFirstRow() == e.getLastRow()
                    && column != ALL_COLUMNS
                    && getSortingOrder(column) == Order.none
                    && modelToView != null) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(YouthTableSorter.this,
                        viewIndex, viewIndex,
                        column, e.getType()));
                return;
            }

            // Something has happened to the data that may have invalidated the row order.
            clearSortingState();
            fireTableDataChanged();
        }
    };

    private static class ColumnSorting {
        private int column;
        private Order order;

        public ColumnSorting(int column, Order order) {
            this.column = column;
            this.order = order;
        }

        public int getComparison(int i) {
            return i * order.getValue();
        }
    }

    public YouthTableSorter(HOTableModel tableModel, JTable table) {
        this.table = table;
        setTableModel(tableModel);
        table.setColumnSelectionAllowed(false);
        setTableHeader(table.getTableHeader());
    }

    public void tableChanged(TableModelEvent event) {
        var tm = getTableModel();
        tm.fireTableChanged(event);
        fireTableDataChanged();
    }

    public void sort() {
        //fireTableChanged(new TableModelEvent(this,  0, this.getRowCount()));
        fireTableDataChanged();
    }
}