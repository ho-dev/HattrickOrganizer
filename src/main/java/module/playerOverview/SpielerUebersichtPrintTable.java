package module.playerOverview;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.model.PlayerOverviewModel;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

public class SpielerUebersichtPrintTable extends JTable {

	private static final long serialVersionUID = -523733130618224089L;
	
	//~ Instance fields ----------------------------------------------------------------------------

    //TableSorter sorter;
    private PlayerOverviewModel m_clTableModel;
    private TableSorter m_clTableSorter;

    //~ Constructors -------------------------------------------------------------------------------

    public SpielerUebersichtPrintTable(PlayerOverviewTable table) {
        super();
        m_clTableSorter = table.getSorter();
        m_clTableModel = (PlayerOverviewModel) m_clTableSorter.getModel();
        initModel(table.getSpaltenreihenfolge());
        setDefaultRenderer(java.lang.Object.class, new HODefaultTableCellRenderer());
        setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Initialisiert das Model
     */
    private void initModel(int[][] spaltenreihenfolge) {
        setOpaque(false);

        setModel(m_clTableSorter);

        final TableColumnModel tableColumnModel = getColumnModel();

        for (int i = 0; i < m_clTableModel.getColumnCount(); i++) {
            tableColumnModel.getColumn(i).setIdentifier(i);
        }

        int[][] targetColumn = spaltenreihenfolge;

        //Reihenfolge -> nach [][1] sortieren
        targetColumn = core.util.Helper.sortintArray(targetColumn, 1);

        if (targetColumn != null) {
            for (int i = 0; i < targetColumn.length; i++) {
                this.moveColumn(getColumnModel().getColumnIndex(targetColumn[i][0]),
                                targetColumn[i][1]);
            }
        }

        m_clTableSorter.addMouseListenerToHeaderInTable(this);

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        m_clTableModel.setColumnsSize(getColumnModel());

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(true);
    }
}
