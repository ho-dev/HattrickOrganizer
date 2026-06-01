// %515857825:de.hattrickorganizer.gui.playeranalysis%
package module.playeranalysis;

import core.db.DBManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.PlayerAnalysisModel;
import core.gui.model.UserColumnController;
import javax.swing.JTable;

final class SpielerMatchesTable extends JTable {

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
		setAutoResizeMode(AUTO_RESIZE_OFF);
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
}
