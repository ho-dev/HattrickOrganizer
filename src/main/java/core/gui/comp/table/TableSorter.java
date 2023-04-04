// %649934645:de.hattrickorganizer.gui.utils%
package core.gui.comp.table;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.model.PlayerOverviewModel;
import core.gui.model.UserColumnFactory;
import core.model.player.Player;
import core.util.HOLogger;
import module.lineup.LineupTableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Decorator for the {@link TableModel} class that ensures that the table data is sorted.
 *
 * <p>The order of the sorted rows are maintained in the <code>indexes</code> instance variable,
 * which is then used to retrieve the <em>i</em>th row by using the mapping between the index
 * in <code>indexes</code> and the index of the row.  For example, if <code>indexes</code> is
 * <code>{ 4, 2, 0, 3, 1 }</code>, the first row is at index 4 in the model, the second is at
 * index 2, etc.  Sorting happens on the values returned by
 * {@link HOTableModel#getValueAt(int, int)}.</p>
 *
 * <p>TableSorter supports a “three-way” sorting approach, based on the number of clicks.
 * If the <code>thirdColSort</code> is set (i.e. equal to or greater than 0), the behaviour is
 * as follows when sorting column with index <code>thirdColSort</code>:</p>
 * <ul>
 *     <li>First click: reverts order,</li>
 *     <li>Second click: back to original order,</li>
 *     <li>Third click: sort by <code>thirdColSort</code> column.</li>
 * </ul>
 */
public class TableSorter extends TableMap {

	@Serial
    private static final long serialVersionUID = 1132334126127788944L;
	private final List<Integer> sortingColumns;
    private int[] indexes;
    private boolean ascending;

    private int currentColumn;

    // index of the column representing the ID
    private final int idColumn;
    private final int m_iInitSortColumnIndex;

    private int thirdColSort = -1; // index of the column using “special” sorting on third click.
    private int iThirdSort = 0; // click count to detect if third click.
    boolean isThirdSort = false;

    public TableSorter(TableModel tablemodel, int idColumn, int initsortcolumnindex) {
        this.idColumn = idColumn;
        this.m_iInitSortColumnIndex = initsortcolumnindex;
        sortingColumns = new ArrayList<>();
        ascending = isAscending(tablemodel, initsortcolumnindex);
        currentColumn = -1;
        setModel(tablemodel);
    }

    public TableSorter(TableModel tablemodel, int idColumn, int initsortcolumnindex, int thirdColSort) {
        this.idColumn = idColumn;
        this.m_iInitSortColumnIndex = initsortcolumnindex;
        sortingColumns = new ArrayList<>();
        this.thirdColSort = thirdColSort;
        ascending = isAscending(tablemodel, initsortcolumnindex);
        currentColumn = -1;
        setModel(tablemodel);
    }

    private boolean isAscending(TableModel tablemodel, int initsortcolumnindex) {
        return tablemodel instanceof HOTableModel &&
                ((HOTableModel)tablemodel).getPositionInArray(UserColumnFactory.BEST_POSITION) != initsortcolumnindex;
    }

    @Override
	public final void setModel(TableModel tablemodel) {
        super.setModel(tablemodel);
        reallocateIndexes();
    }

    /**
     * Maps a match id to the row that contains it.
     *
     * @param matchid ID of the match to find.
     * @return int – Row in the table containing the match's details.
     *               Returns -1 if the match <code>matchid</code> cannot be found.
     */
    public final int getRow4Match(int matchid) {
        if (matchid > 0) {
            for (int i = 0; i < getRowCount(); i++) {
                try {
                    var entry = (ColorLabelEntry) getValueAt(i, idColumn);
                    if ( entry != null) {
                        if (matchid == (int) entry.getNumber()) {
                            return indexes[i];
                        }
                    }
                } catch (Exception e) {
                    HOLogger.instance().log(getClass(),"TableSorter.getRow4Match: " + e);
                }
            }
        }

        return -1;
    }

    /**
     * Maps a player id to the row that contains his entry.
     *
     * @param spielerid ID of the player to find.
     * @return int – Row in the table containing the player's details.
     *               Returns -1 if the player with id <code>spielerid</code> cannot be found.
     */
    public final int getRow4Spieler(int spielerid) {
        // Can be negative if the player is a temporary player (for ex. in transfer scout).
        if (spielerid != 0) {
            for (int i = 0; i < getRowCount(); i++) {
                try {
                    var entry = (ColorLabelEntry) getValueAt(i, idColumn);
                    if ( entry != null) {
                        if (spielerid == Integer.parseInt(entry.getText())) {
                            return i;
                        }
                    }
                } catch (Exception e) {
                    HOLogger.instance().log(getClass(),"TableSorter.getRow4Spieler: " + e);
                }
            }
        }

        return -1;
    }

    public final module.transfer.scout.ScoutEintrag getScoutEintrag(int row) {
        if (row > -1) {
            try {
                var entry = (ColorLabelEntry) getValueAt(row, idColumn);
                if ( entry != null) {
                    return ((module.transfer.scout.TransferTableModel) getModel())
                            .getScoutEintrag(Integer.parseInt(entry.getText()));
                }
            } catch (Exception e) {
                HOLogger.instance().log(getClass(),"TableSorter.getScoutEintrag: " + e);
                return null;
            }
        }

        return null;
    }

    public final Player getSpieler(int row) {
        if (row > -1) {
            try {
                var entry = (ColorLabelEntry) getValueAt(row, idColumn);
                if ( entry != null ) {
                    var text = entry.getText();
                    if (text != null && !text.isEmpty()) {
                        final int id = Integer.parseInt(text);
                        if (getModel() instanceof PlayerOverviewModel) {
                            return ((PlayerOverviewModel) getModel()).getPlayer(id);
                        } else if (getModel() instanceof LineupTableModel) {
                            return ((LineupTableModel) getModel()).getPlayer(id);
                        } else {
                            throw new Exception("Tablemodel unbekannt!");
                        }
                    }
                }
            } catch (Exception e) {
                HOLogger.instance().log(getClass(),e);
                return null;
            }
        }
        return null;
    }

    @Override
	public final void setValueAt(Object obj, int i, int j) {
    	getModel().setValueAt(obj, indexes[i], j);
    }

    @Override
	public final Object getValueAt(int i, int j) {
        if (indexes.length <= i || i < 0) {
            return null;
        }
        if (getRowCount() <= indexes[i] || getColumnCount() <= j) {
            return null;
        }
        if (j < 0) {
            return null;
        }

        return getModel().getValueAt(indexes[i], j);
    }

    /**
     * Registers mouse listener on <code>jtable</code> to handle sorting.
     *
     * @param jtable Table to add listener to.
     */
    public final void addMouseListenerToHeaderInTable(JTable jtable) {
        final JTable tableView = jtable;
        final JTableHeader jtableheader = tableView.getTableHeader();

        // Listener already present.
        if (jtableheader.getComponentListeners().length > 0) {
            return;
        }

        tableView.setColumnSelectionAllowed(false);

        final MouseAdapter mouseadapter = new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent mouseevent) {
                final TableColumnModel tablecolumnmodel = tableView.getColumnModel();
                final int i = tablecolumnmodel.getColumnIndexAtX(mouseevent.getX());
                if ( i > -1) {
                    final int j = tableView.convertColumnIndexToModel(i);

                    if ((mouseevent.getClickCount() == 1) && (j != -1)) {
                        boolean flag = ascending;

                        // If column sorted is different to the current one, sort by natural order.
                        if (currentColumn != j) {
                            flag = isAscending(getModel(), j);
                        }

                        if (thirdColSort == i) {
                            isThirdSort = false;
                            iThirdSort++;
                            if (iThirdSort == 3) {
                                isThirdSort = true;
                                flag = true;
                                iThirdSort = 0;
                            }
                        }

                        if (currentColumn == j) {
                            flag = !flag;
                        }

                        TableSorter.this.sortByColumn(j, flag);
                    }
                }
            }
        };

        jtableheader.addMouseListener(mouseadapter);
    }

    public final int compare(int i, int j) {

        for (final Integer integer : sortingColumns) {
            final int l = compareRowsByColumn(i, j, integer);

            if (l != 0) {
                return ascending ? l : (-l);
            }
        }

        return 0;
    }

    private int compareRowsByColumn(int i, int j, int k) {
        final Object obj = getModel().getValueAt(i, k);
        final Object obj1 = getModel().getValueAt(j, k);

        if ((obj == null) && (obj1 == null)) {
            return 0;
        }

        if (obj == null) {
            return -1;
        }

        if (obj1 == null) {
            return 1;
        }

        if (obj instanceof IHOTableEntry
            && obj1 instanceof IHOTableEntry) {
            final IHOTableEntry colorLabelentry1 = (IHOTableEntry) getModel().getValueAt(i, k);
            final IHOTableEntry colorLabelentry2 = (IHOTableEntry) getModel().getValueAt(j, k);
            return isThirdSort ?
                    colorLabelentry1.compareToThird(colorLabelentry2) :
                    colorLabelentry1.compareTo(colorLabelentry2);
        }

        final Object obj2 = getModel().getValueAt(i, k);
        final String s2 = obj2.toString();
        final Object obj3 = getModel().getValueAt(j, k);
        final String s3 = obj3.toString();
        final int i2 = s2.compareTo(s3);

        if (i2 < 0) {
            return -1;
        }

        return (i2 <= 0) ? 0 : 1;

    }

    public final void initsort() {
        // Sort, including when m_iInitSortColumnIndex is first column
        if (m_iInitSortColumnIndex >= 0) {
            final boolean flag = ascending;
            sortByColumn(m_iInitSortColumnIndex, flag);
        }
    }

    public final void reallocateIndexes() {
        final int i = getModel().getRowCount();
        indexes = new int[i];

        for (int j = 0; j < i; j++) {
            indexes[j] = j;
        }
    }

    /**
     * Sorts the data from the model based on the values stored in <code>sortingColumns</code>
     * (using {@link #compare(int, int)}), and stores the resulting order of rows in <code>indexes</code>.
     */
    public final void shuttlesort(int[] ai, int[] ai1, int i, int j) {
        if ((j - i) < 2) {
            return;
        }

        final int k = (i + j) / 2;
        shuttlesort(ai1, ai, i, k);
        shuttlesort(ai1, ai, k, j);

        int l = i;
        int i1 = k;

        if (((j - i) >= 4) && (compare(ai[k - 1], ai[k]) <= 0)) {
            if (j - i >= 0) System.arraycopy(ai, i, ai1, i, j - i);
            return;
        }

        for (int k1 = i; k1 < j; k1++) {
            if ((i1 >= j) || ((l < k) && (compare(ai[l], ai[i1]) <= 0))) {
                ai1[k1] = ai[l++];
            } else {
                ai1[k1] = ai[i1++];
            }
        }
    }

    private void sortByColumn(int i, boolean flag) {
        ascending = flag;
        currentColumn = i;
        sortingColumns.clear();
        sortingColumns.add(i);
        shuttlesort(indexes.clone(), indexes, 0, indexes.length);
        super.tableChanged(new TableModelEvent(this));
    }

    @Override
	public final void tableChanged(TableModelEvent tablemodelevent) {
        reallocateIndexes();
        super.tableChanged(tablemodelevent);
    }
}
