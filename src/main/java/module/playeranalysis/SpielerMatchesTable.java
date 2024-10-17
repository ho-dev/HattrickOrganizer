// %515857825:de.hattrickorganizer.gui.playeranalysis%
package module.playeranalysis;

import core.db.DBManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.PlayerAnalysisModel;
import core.gui.model.UserColumnController;
import javax.swing.JTable;
import java.io.Serial;

final class SpielerMatchesTable extends JTable {

	@Serial
	private static final long serialVersionUID = 5959815846371146851L;
	private final PlayerAnalysisModel m_clTableModel;
	private int playerId;

    /**
	 * Creates a new SpielerMatchesTable object.
	 */
	SpielerMatchesTable(int spielerid, int instance) {
		super();
        playerId = spielerid;
		m_clTableModel = UserColumnController.instance().getAnalysisModel(instance);
		m_clTableModel.setValues(DBManager.instance().getPlayerMatchCBItems(playerId));
		m_clTableModel.initTable(this);
		setOpaque(false);
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
//		setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
	}

	void storeUserSettings() {
        m_clTableModel.storeUserSettings();
	}

	void refresh(int spielerid) {
		playerId = spielerid;
		m_clTableModel.setValues(DBManager.instance().getPlayerMatchCBItems(playerId));
		m_clTableModel.fireTableDataChanged();
		repaint();
	}

	/**
	 * Initialisiert das Model
	 */
//	private void initModel() {

//		if (m_clTableModel == null) {
//			m_clTableModel = (instance == 1) ? UserColumnController.instance().getAnalysis1Model()
//					: UserColumnController.instance().getAnalysis2Model();
//			m_clTableModel.setValues(DBManager.instance().getPlayerMatchCBItems(m_iSpielerId));
//
//			m_clTableSorter = new TableSorter(m_clTableModel, -1, -1);
//
//			ToolTipHeader header = new ToolTipHeader(getColumnModel());
//			header.setToolTipStrings(m_clTableModel.getTooltips());
//			header.setToolTipText("");
//			setTableHeader(header);
//
//			setModel(m_clTableSorter);
//
//
//			for (int i=0; i<getColumnModel().getColumnCount(); i++){
//				var tm = m_clTableModel.getDisplayedColumns()[i];
//				var cm = this.getColumnModel().getColumn(i);
//				cm.setIdentifier(tm.getId());
//			}
//
////			final TableColumnModel columModel = getColumnModel();
//
////			for (int i = 0; i < m_clTableModel.getColumnCount(); i++) {
////				columModel.getColumn(i).setIdentifier(i);
////			}
//
//			m_clTableModel.initColumnOrder(this);
//			m_clTableSorter.addMouseListenerToHeaderInTable(this);
//			m_clTableModel.setColumnsSize(getColumnModel());
//		} else {
//			// Werte neu setzen
//			m_clTableModel.setValues(DBManager.instance().getPlayerMatchCBItems(m_iSpielerId));
//			m_clTableSorter.reallocateIndexes();
//		}

//		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		setSelectionMode(0);
//		setRowSelectionAllowed(true);
//		m_clTableSorter.initsort();
//	}

}
