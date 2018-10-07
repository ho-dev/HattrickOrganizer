package module.evilcard.gui;

import core.model.HOVerwaltung;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

class PlayersTable extends JTable {

	private static final long serialVersionUID = -4586795480386271861L;
	private String[] columnToolTips = null;

	PlayersTable() {
		super();

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		columnToolTips = new String[PlayersTableModel.cols];
		columnToolTips[PlayersTableModel.COL_WARNINGS] = HOVerwaltung.instance().getLanguageString(
				"GelbeKarten");
		columnToolTips[PlayersTableModel.COL_WARNINGS_TYPE1] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType1");
		columnToolTips[PlayersTableModel.COL_WARNINGS_TYPE2] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType2");
		columnToolTips[PlayersTableModel.COL_WARNINGS_TYPE3] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType3");
		columnToolTips[PlayersTableModel.COL_WARNINGS_TYPE4] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WarningType4");
		columnToolTips[PlayersTableModel.COL_DIRECT_RED_CARDS] = HOVerwaltung.instance()
				.getLanguageString("RoteKarten");
		columnToolTips[PlayersTableModel.COL_CARDS] = HOVerwaltung.instance().getLanguageString(
				"tooltip.CardsTotal");
		columnToolTips[PlayersTableModel.COL_MATCHES] = HOVerwaltung.instance().getLanguageString(
				"tooltip.MatchCount");
		columnToolTips[PlayersTableModel.COL_RAW_AVERAGE] = HOVerwaltung.instance()
				.getLanguageString("tooltip.RawAverage");
		columnToolTips[PlayersTableModel.COL_WEIGHTED_AVERAGE] = HOVerwaltung.instance()
				.getLanguageString("tooltip.WeightedAverage");
	}

	// Implement table header tool tips.
	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {

			private static final long serialVersionUID = -7727941309922344526L;

			@Override
			public String getToolTipText(MouseEvent e) {
				// String tip = null;
				java.awt.Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				int realIndex = columnModel.getColumn(index).getModelIndex();
				return columnToolTips[realIndex];
			}
		};
	}
}
