// %1896649635:de.hattrickorganizer.gui.statistic%
package module.statistics;

import core.db.DBManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.comp.table.ToolTipHeader;
import core.gui.model.ArenaStatistikTableModel;
import core.util.Helper;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;



/**
 * Table for arena statistics.
 */
public class ArenaStatistikTable extends JTable {

	private static final long serialVersionUID = -6319111452810917050L;

    //~ Instance fields ----------------------------------------------------------------------------

	private ArenaStatistikTableModel m_clTableModel;
    private TableSorter m_clTableSorter;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ArenaStatistikTable object.
     *
     * @param matchtyp matches typ (e.g. own league matches only)
     */
    public ArenaStatistikTable(int matchtyp) {
        super();
        setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
        setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
        initModel(matchtyp);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the table sorter.
     */
    public final TableSorter getSorter() {
        return m_clTableSorter;
    }

    /**
     * Markiert ein Match, wenn es in der Tabelle vorhanden ist, sonst wird die Selektion gelöscht
     */
    public final void markiereMatch(int matchid) {
        final int row = m_clTableSorter.getRow4Match(matchid);

        if (row > -1) {
            setRowSelectionInterval(row, row);
        } else {
            clearSelection();
        }
    }

    //----------------Refresh-------------------------------------------

    /**
     * Refresh all data.
     *
     * @param matchtypen matches typ (e.g. own league matches only)
     */
    public final void refresh(int matchtypen) {
    	reInitModel(matchtypen);
        repaint();
    }

    /**
     * Initialisiert das Model
     *
     * @param matchtyp matches typ (e.g. own league matches only)
     */
    private void initModel(int matchtyp) {
        setOpaque(false);

        reInitModel(matchtyp);

        final ToolTipHeader header = new ToolTipHeader(getColumnModel());
        header.setToolTipStrings(m_clTableModel.m_sToolTipStrings);
        header.setToolTipText("");
        setTableHeader(header);

        final TableColumnModel tableColumnModel = getColumnModel();

        for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
            tableColumnModel.getColumn(i).setIdentifier(i);
        }

        m_clTableSorter.addMouseListenerToHeaderInTable(this);

        setAutoResizeMode(AUTO_RESIZE_OFF);
        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(0)).setPreferredWidth(Helper.calcCellWidth(85));
        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(0)).setMinWidth(Helper.calcCellWidth(70));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(1)).setPreferredWidth(Helper.calcCellWidth(20));
        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(1)).setMinWidth(Helper.calcCellWidth(20));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(2)).setMinWidth(Helper.calcCellWidth(55));
        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(2)).setPreferredWidth(Helper.calcCellWidth(120));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(3)).setMinWidth(Helper.calcCellWidth(55));
        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(3)).setPreferredWidth(Helper.calcCellWidth(60));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(4)).setMinWidth(Helper.calcCellWidth(25));
        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(4)).setPreferredWidth(Helper.calcCellWidth(25));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(5)).setPreferredWidth(Helper.calcCellWidth(85));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(6)).setPreferredWidth(Helper.calcCellWidth(150));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(7)).setPreferredWidth(Helper.calcCellWidth(150));

        tableColumnModel.getColumn(tableColumnModel.getColumnIndex(8)).setPreferredWidth(Helper.calcCellWidth(110));

        for (int i = 9; i < tableColumnModel.getColumnCount(); i++) {
            tableColumnModel.getColumn(tableColumnModel.getColumnIndex(i)).setPreferredWidth(Helper.calcCellWidth(90));
        }

        setSelectionMode(0);
        setRowSelectionAllowed(true);

        m_clTableSorter.initsort();
    }

    private void reInitModel(int matchtyp) {
        m_clTableModel = DBManager.instance().getArenaStatistikModel(matchtyp);
        if (m_clTableSorter == null){
            m_clTableSorter = new TableSorter(m_clTableModel, 5, -1);
            setModel(m_clTableSorter);
        }else{
            m_clTableSorter.setModel(m_clTableModel);
            setModel(m_clTableSorter);
            m_clTableSorter.reallocateIndexes();
        }
    }
    
}
