// %649934645:de.hattrickorganizer.gui.utils%
package core.gui.comp.table;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.model.PlayerOverviewModel;
import core.model.player.Spieler;
import core.util.HOLogger;
import module.lineup.LineupTableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableSorter extends TableMap {

	private static final long serialVersionUID = 1132334126127788944L;
	private Vector<Integer> sortingColumns;
    private int[] indexes;
    private boolean ascending;

    private int currentColumn;
    private int idSpalte;
    private int m_iInitSortColumnIndex = -1;

    public TableSorter() {
        sortingColumns = new Vector<Integer>();
        ascending = false;
        currentColumn = -1;
        indexes = new int[0];
    }

    public TableSorter(TableModel tablemodel, int idSpalte, int initsortcolumnindex) {
        this.idSpalte = idSpalte;
        this.m_iInitSortColumnIndex = initsortcolumnindex;
        sortingColumns = new Vector<Integer>();
        ascending = false;
        currentColumn = -1;
        setModel(tablemodel);
    }

    @Override
	public final void setModel(TableModel tablemodel) {
        super.setModel(tablemodel);
        reallocateIndexes();
    }

    public final int getRow4Match(int matchid) {
        if (matchid > 0) {
            for (int i = 0; i < getRowCount(); i++) {
                try {
                    if (matchid == (int) ((ColorLabelEntry) getValueAt(i,idSpalte))
                                   .getNumber()) {
                        //Die Zeile zurückgeben, muss vorher gemapped werden
                        return indexes[i];
                    }
                } catch (Exception e) {
                    HOLogger.instance().log(getClass(),"TableSorter.getRow4Match: " + e);
                }
            }
        }

        return -1;
    }

    public final int getRow4Spieler(int spielerid) {
        //Kann < 0 für Tempspieler sein if ( spielerid > 0 )
        if (spielerid != 0) {
            for (int i = 0; i < getRowCount(); i++) {
                try {
                    if (spielerid == Integer.parseInt(((core.gui.comp.entry.ColorLabelEntry) getValueAt(i,
                                                                                                                       idSpalte))
                                                      .getText())) {
                        //Die Zeile zurückgeben, muss vorher gemapped werden
                        // indexes[i];
                        return i;
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
                return ((module.transfer.scout.TransferTableModel) getModel())
                       .getScoutEintrag(Integer.parseInt(((ColorLabelEntry) getValueAt(row, idSpalte)).getText()));
            } catch (Exception e) {
                HOLogger.instance().log(getClass(),"TableSorter.getScoutEintrag: " + e);
                return null;
            }
        }

        return null;
    }


    public final Spieler getSpieler(int row) {
        if (row > -1) {
            try {
            	final int id = Integer.parseInt(((ColorLabelEntry) getValueAt(row,idSpalte)).getText());

                
                if (getModel() instanceof PlayerOverviewModel) {
                    return ((PlayerOverviewModel) getModel()).getSpieler(id);
                } else if (getModel() instanceof LineupTableModel) {
                    return ((LineupTableModel) getModel()).getSpieler(id);
                } else {
                    throw new Exception("Tablemodel umbekannt!");
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

        if ((i < 0) || (j < 0)) {
            return null;
        } 
        
        return getModel().getValueAt(indexes[i], j);
       
    }

    public final void addMouseListenerToHeaderInTable(JTable jtable) {
        final TableSorter sorter = this;
        final JTable tableView = jtable;

        final JTableHeader jtableheader = tableView.getTableHeader();

        //Listener schon vorhanden
        if (jtableheader.getComponentListeners().length > 0) {
            return;
        }

        tableView.setColumnSelectionAllowed(false);

        final MouseAdapter mouseadapter = new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent mouseevent) {
                final TableColumnModel tablecolumnmodel = tableView.getColumnModel();
                final int i = tablecolumnmodel.getColumnIndexAtX(mouseevent.getX());
                final int j = tableView.convertColumnIndexToModel(i);

                if ((mouseevent.getClickCount() == 1) && (j != -1)) {
                    boolean flag = ascending;

                    if (currentColumn == j) {
                        flag = !flag;
                    }

                    sorter.sortByColumn(j, flag);
                }
            }
        };

        jtableheader.addMouseListener(mouseadapter);
    }

    public final int compare(int i, int j) {

        for (int k = 0; k < sortingColumns.size(); k++) {
            final Integer integer = sortingColumns.elementAt(k);
            final int l = compareRowsByColumn(i, j, integer.intValue());

            if (l != 0) {
                return ascending ? l : (-l);
            }
        }

        return 0;
    }

    private final int compareRowsByColumn(int i, int j, int k) {
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
            return colorLabelentry1.compareTo(colorLabelentry2);
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
        //InitSortColumnIndex
        if (m_iInitSortColumnIndex > 0) {
            final int j = m_iInitSortColumnIndex;
            final boolean flag = ascending;
            sortByColumn(j, flag);
        }
    }


    public final void reallocateIndexes() {
        final int i = getModel().getRowCount();
        indexes = new int[i];

        for (int j = 0; j < i; j++) {
            indexes[j] = j;
        }
    }

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
            for (int j1 = i; j1 < j; j1++) {
                ai1[j1] = ai[j1];
            }

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

    private final void sortByColumn(int i, boolean flag) {
        ascending = flag;
        currentColumn = i;
        sortingColumns.removeAllElements();
        sortingColumns.addElement(Integer.valueOf(i));
        shuttlesort(indexes.clone(), indexes, 0, indexes.length);
        super.tableChanged(new TableModelEvent(this));
    }

    @Override
	public final void tableChanged(TableModelEvent tablemodelevent) {
        reallocateIndexes();
        super.tableChanged(tablemodelevent);
    }
}
