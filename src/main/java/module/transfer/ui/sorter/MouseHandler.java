package module.transfer.ui.sorter;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MouseHandler extends MouseAdapter {
    private final AbstractTableSorter sorter;

    /**
     * Creates a new MouseHandler object.
     *
     * @param sorter AbstractTableSorter
     */
    MouseHandler(AbstractTableSorter sorter) {
        this.sorter = sorter;

        // 
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        JTableHeader h = (JTableHeader) e.getSource();
        TableColumnModel columnModel = h.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX(e.getX());
        if ( viewColumn > -1 ) {
            int column = columnModel.getColumn(viewColumn).getModelIndex();

            //skip unwanted columns
            if (column < this.sorter.minSortableColumn()) {
                column = -1;
            }

            if (column != -1) {
                int status = this.sorter.getSortingStatus(column);

                if (!e.isControlDown()) {
                    this.sorter.cancelSorting();
                }

                // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
                // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
                status = status + (e.isShiftDown() ? (-1) : 1);
                status = ((status + 4) % 3) - 1; // signed mod, returning {-1, 0, 1}
                this.sorter.setSortingStatus(column, status);
            }
        }
    }
}
