package module.transfer.ui.sorter;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

class TableModelHandler implements TableModelListener {
    private final AbstractTableSorter sorter;

    /**
     * Creates a new TableModelHandler object.
     *
     * @param sorter
     */
    TableModelHandler(AbstractTableSorter sorter) {
        this.sorter = sorter;
    }

    public void tableChanged(TableModelEvent e) {
        // If we're not sorting by anything, just pass the event along.             
        if (!this.sorter.isSorting()) {
            this.sorter.clearSortingState();
            this.sorter.fireTableChanged(e);

            return;
        }

        // If the table structure has changed, cancel the sorting; the             
        // sorting columns may have been either moved or deleted from             
        // the model. 
        if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
            this.sorter.cancelSorting();
            this.sorter.fireTableChanged(e);

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

        if ((e.getFirstRow() == e.getLastRow())
            && (column != TableModelEvent.ALL_COLUMNS)
            && (this.sorter.getSortingStatus(column) == AbstractTableSorter.NOT_SORTED)
            && (this.sorter.getModelToView() != null)) {
            int viewIndex = this.sorter.getModelToView()[e.getFirstRow()];

            this.sorter.fireTableChanged(new TableModelEvent(this.sorter,
                    viewIndex, viewIndex, column, e.getType()));

            return;
        }

        // Something has happened to the data that may have invalidated the row order. 
        this.sorter.clearSortingState();
        this.sorter.fireTableDataChanged();

        return;
    }
}
