package module.matches;

import core.db.DBManager;
import core.gui.model.MatchesColumnModel;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchKurzInfo;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

final class MatchesTable extends JTable {

	private final MatchesColumnModel m_clTableModel;

	MatchesTable(int matchtyp) {
		super();
		setOpaque(false);
		m_clTableModel = UserColumnController.instance().getMatchesModel();
		var matches = DBManager.instance().getMatchesKurzInfo(HOVerwaltung.instance().getModel().getBasics().getTeamId(), matchtyp, UserParameter.instance().matchLocation).toArray(new MatchKurzInfo[0]);
		m_clTableModel.setValues(matches);
		m_clTableModel.initTable(this);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	public void storeUserSettings() {
		m_clTableModel.storeUserSettings();
	}

	public void refresh(int iMatchType, MatchLocation matchLocation) {
		m_clTableModel.setValues(DBManager.instance().getMatchesKurzInfo(
				HOVerwaltung.instance().getModel().getBasics().getTeamId(), iMatchType, matchLocation).toArray(new MatchKurzInfo[0]));
		m_clTableModel.fireTableDataChanged();
	}

	void markiereMatch(int matchid) {
		final int row = m_clTableModel.getRowIndexOfMatch(matchid);
		if (row > -1) {
			setRowSelectionInterval(row, row);
		} else {
			clearSelection();
		}
	}

	public MatchKurzInfo getMatchAtRow(int selectedRowNumber) {
		return  m_clTableModel.getMatchAtRow(selectedRowNumber);
	}
}
